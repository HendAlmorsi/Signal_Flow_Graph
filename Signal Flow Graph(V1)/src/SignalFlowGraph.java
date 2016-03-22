import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/*import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
*/

public class SignalFlowGraph {
	
	double[][] adjacency_matrix;
	Edge[] edges;
	Node[] nodes;
	int sinkID;
	int sourceID;
	int[] parent;
	boolean[] visited;
	ArrayList<Loop> loops;
	ArrayList<ForwardPath> forward_paths;
	boolean[] taken; //used in combination of loops
	int[] selection; //used in combination of loops
	double accumulator; //used in combination of loops
	double Delta;
	ArrayList<Double> ds;
	ArrayList<Loop> temp_loops;
	ArrayList<ArrayList<Loop>> combinations;
	
	
	public SignalFlowGraph(int n, Node[] no , Edge[] e){
		adjacency_matrix = new double[n][n];
		combinations = new ArrayList<ArrayList<Loop>>();
		for(int i=0;i<n;i++){
			Arrays.fill(adjacency_matrix[i], 0);			
		}
		nodes = no;
		edges = e;
		for(int i=0;i<edges.length;i++){
			if(Math.abs(adjacency_matrix[edges[i].start.id][edges[i].end.id] - 0) < 1e-7){
				adjacency_matrix[edges[i].start.id][edges[i].end.id] = edges[i].weight;				
			}
			else {
				adjacency_matrix[edges[i].start.id][edges[i].end.id] += edges[i].weight;
			}
		}
		sinkID = getSink();
		sourceID = getSource();
		visited = new boolean[n];
		parent = new int[n];
		for(int i=0;i<n;i++) {
			parent[i] = i;
		}
		loops = new ArrayList<Loop>();
		forward_paths = new ArrayList<ForwardPath>();
		find_forward_paths(sourceID);
		for(int i=0;i<n;i++) {
			parent[i] = i;
		}
		find_loops(sourceID);
		//calculate deltas
		
		Delta = findDelta(loops);
		ds = new ArrayList<Double>();
		for(int i=0;i<forward_paths.size();i++){
			temp_loops = new ArrayList<Loop>();
			for(int j=0;j<loops.size();j++){
				HashSet<Integer> t = new HashSet<Integer>();
				for(int k=0;k<loops.get(j).loop.length;k++){				
					t.add(loops.get(j).loop[k]);
				}
				int counter = 0;
				for(int k=0;k<forward_paths.get(i).path.length;k++){
					if(t.contains(forward_paths.get(i).path[k])){
						//don't add
						break;
					}
					counter++;
				}
				if(counter==  forward_paths.get(i).path.length){
					temp_loops.add(loops.get(j));
				}
			}
			ds.add(findDelta(temp_loops));
		}
		/*
		System.out.println(Mason());
		System.out.println(forward_paths.size());
		System.out.println(loops.size());
		*/
	}
	
	public double Mason(){
		double result = 0;
		for(int i=0;i<forward_paths.size();i++){
			result += (forward_paths.get(i).path_gain * ds.get(i));
		}
		return result/Delta;
	}
	public double findDelta(ArrayList<Loop> l){
		double delta = 1;
		double sum = 0;
		for(int i=1;i<=l.size();i++){
			selection = new int[i];
			taken = new boolean[l.size()];
			accumulator = 0;
			sum_combinations(l,i , 0,0);			
			if((i%2)==1){
				sum-=accumulator;
			}
			else{
				sum+=accumulator;
			} 
		}
		delta += sum;
		return delta;
	}
	private void sum_combinations(ArrayList<Loop> l,int needed, int index , int last) {
		if(index == needed){
			ArrayList<Loop> temp = new ArrayList<Loop>();
			double s = 1;
			for(int i=0;i<needed;i++){
				s*=l.get(selection[i]).loop_gain;
				temp.add(l.get(selection[i]));
			}
			if(!combinations.contains(temp))
				combinations.add(temp);
			accumulator += s;
			return;
		}
		else{
			for(int i = last ; i<l.size();i++){
				if(!taken[i] && isNonTouching(i,l,index)){
					selection[index] = i;
					taken[i] = true;
					sum_combinations(l, needed, index+1, i+1);
					taken[i] = false;
				}
			}
		}
		return ;
	}

	private boolean isNonTouching(int i,ArrayList<Loop> loops,int index) {
		for(int j=0;j<index;j++){
			Loop temp = loops.get(selection[j]);
			for(int k=0;k<loops.get(i).loop.length;k++){
				for(int t=0;t<temp.loop.length;t++){
					if(loops.get(i).loop[k] == temp.loop[t]){
						return false;
					}
				}
			}	
		}
		return true;
	}

	public int getSink(){
		int count;
		for(int i=0;i<nodes.length;i++) {
			count = 0;
			for(int j=0;j<nodes.length;j++) {
				if(Math.abs(adjacency_matrix[i][j] - 0)> 1e-7)count++;
			}
			if(count==0) {
				return i;
			}
		}
		//the method would return at sink and a different case should not happen
		return 0;
	}
	
	public int getSource() {
		int count;
		for(int i=0;i<nodes.length;i++) {
			count = 0;
			for(int j=0;j<nodes.length;j++) {
				if(adjacency_matrix[j][i]!=0)count++;
			}
			if(count==0) {
				return i;
			}
		}
		//the method would return at sink and a different case should not happen
		return 0;
	}
	
	public void find_forward_paths(int i) {
		visited[i] = true;
		if(i==sinkID) {
			CreateForwardPath();
		}
		else {
			for(int j=0;j<nodes.length;j++) {
				if(!visited[j] && adjacency_matrix[i][j]!=0){
					parent[j] = i;
					find_forward_paths(j);
				}
			}
		}
		visited[i] = false;
	}
	
	public void CreateForwardPath() {
		ArrayList<Integer> reversed = new ArrayList<Integer>();
		int v = sinkID;
		double m = 1;
		while(v!=sourceID) {
			reversed.add(v);
			m *= adjacency_matrix[parent[v]][v];
			v = parent[v];
		}
		reversed.add(sourceID);
		int[] p = new int[reversed.size()];
		for(int i=0;i<reversed.size();i++) {
			p[i] = reversed.get(reversed.size()-1-i);
		}
		ForwardPath f = new ForwardPath(p,m);
		forward_paths.add(f);
	}

	public void find_loops(int i) {
		if(visited[i] == true) {
			CreateLoop(i);
			return;
		}
		else {
			visited[i] = true;
			for(int j=0;j<nodes.length;j++) {
				if(Math.abs(adjacency_matrix[i][j]-0)>1e-7){
					parent[j] = i;
					find_loops(j);
				}
			}
		}
		visited[i] = false;
	}
	
	private void CreateLoop(int i) {
		ArrayList<Integer> reversed = new ArrayList<Integer>();
		reversed.add(i);
		int start = i;
		int v = parent[start];
		double m = 1;
		while(v!= i) {
			reversed.add(v);
			m *= adjacency_matrix[v][start];
			start = v;
			v = parent[start];
		}
		m*= adjacency_matrix[i][start];
		reversed.add(i);
		int[] p = new int[reversed.size()];
		for(int j=0;j<reversed.size();j++) {
			p[j] = reversed.get(reversed.size()-1-j);
		}
		boolean unique = true;
		int[] q = p.clone();
		for(int k=0;k<loops.size();k++) {
			if(loops.get(k).loop.length == q.length) {
				unique = ((unique) && (!same_Loop(q,loops.get(k).loop )));
			}
			if(!unique) break;
		}
		if(unique) {
			Loop l = new Loop(p,m);
			loops.add(l);
		}
	}

	public boolean same_Loop(int[] A,int[] B) {
		Arrays.sort(A);
		Arrays.sort(B);
		HashSet<Integer> Ad = new HashSet<Integer>();
		for(int i=0;i<A.length;i++) {
			Ad.add(A[i]);
		}
		for(int i=0;i<B.length;i++) {
			if(!Ad.contains(B[i])) {
				return false;
			}
		}
		return true;
	}
/*	public static void main(String[] args){
		Node n0 = new Node(0,0.5,0);
		Node n1 = new Node(1,0.5,1);
		Node n2= new Node(2,0.5,2);
		Node n3 = new Node(3,0.5,3);
		Node n4 = new Node(4,0.5,4);
		Node n5 = new Node(5,0.5,0.5);
		Node n6 = new Node(6,0.5,0.5);
		Node n7 = new Node(7,0.5,0.5);
		
		Node[] na = new Node[5];
		na[0] = n0;
		na[1] = n1;
		na[2] = n2;
		na[3] = n3;
		
		na[4] = n4;
		
		na[5] = n5;
		na[6] = n6;
		na[7] = n7;
		
		Edge[] ee = new Edge[7];
		Edge e0 = new Edge(na[0], na[1], 1);
		Edge e1 = new Edge(na[1], na[2], 1);
		Edge e2 = new Edge(na[2], na[3], 1);
		Edge e3 = new Edge(na[3], na[4], 1);
		Edge e4 = new Edge(na[2], na[1], 1);
		Edge e5 = new Edge(na[3], na[2], 1);
		Edge e6 = new Edge(na[3], na[1], 1);
		Edge e5 = new Edge(3, 4, 1);
		Edge e6 = new Edge(3,6,-4);
		Edge e7 = new Edge(4,5,0.5);
		Edge e8 = new Edge(5,4,-5);
		Edge e9 = new Edge(5,6,3);
		Edge e10 = new Edge(5,7,1);
		
		ee[0] = e0;
		ee[1] = e1;
		ee[2] = e2;
		ee[3] = e3;
		ee[4] = e4;
		ee[5] = e5;
		ee[6] = e6;
		
		ee[5] = e5;
		ee[6] = e6;
		ee[7] = e7;
		ee[8] = e8;
		ee[9] = e9;
		ee[10] = e10;
		
		new SignalFlowGraph(5,na,ee);
	}*/
}