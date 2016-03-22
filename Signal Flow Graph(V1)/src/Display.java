import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.LayoutStyle.*;
import javax.swing.GroupLayout.*;
import javax.swing.event.*;
import javax.swing.border.*;


public class Display extends JFrame {
	private JTextField txtWeight;
	private SignalFlowGraph signalSolver;
	private DefaultListModel pathsLoops = new DefaultListModel<>();
	private int signIndex = 0;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Display frame = new Display();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Display() {
		setResizable(false);
		setTitle("Signal Flow Graph");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 942, 618);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel.setLayout(new BorderLayout(0, 0));
		panel.setSize(650, 380);
		
		CurveApplet curveApplet = new CurveApplet(panel.getWidth(), panel.getHeight());
		panel.add(curveApplet);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
		
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Calibri", Font.PLAIN, 14));
		textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250, 250));
        areaScrollPane.setBorder(
            new LineBorder(new Color(192, 192, 192), 2, true));

		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 653, GroupLayout.PREFERRED_SIZE)
						.addComponent(areaScrollPane, GroupLayout.PREFERRED_SIZE, 653, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 384, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(areaScrollPane, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		
		JList list = new JList(pathsLoops);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//System.out.println(list.getSelectedIndex());
			}
		});
		
        JScrollPane listScrollPane = new JScrollPane(list);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        listScrollPane.setPreferredSize(new Dimension(250, 250));
        listScrollPane.setBorder(
            new LineBorder(new Color(192, 192, 192), 2, true));
		
		JButton btnPreview = new JButton("Preview");
		btnPreview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(!list.isSelectionEmpty()){
					curveApplet.action = 2;
					curveApplet.pathLoopToPrint = list.getSelectedIndex();
					curveApplet.pane.repaint();
					if((signalSolver.combinations.size() != 0) && (list.getSelectedIndex() >= (signalSolver.forward_paths.size()+ signalSolver.loops.size()))){
						double gain = 1;
						for(int i = 0; i < signalSolver.combinations.get(list.getSelectedIndex() - signalSolver.forward_paths.size() - signalSolver.loops.size()).size() ; i++){
							gain *= signalSolver.combinations.get(list.getSelectedIndex() - signalSolver.forward_paths.size() - signalSolver.loops.size()).get(i).loop_gain;
						}
						
						textArea.append("Non Touching loop Gain = " + gain + "\n");

					}
					else if((signalSolver.loops.size() != 0) && (list.getSelectedIndex() >= signalSolver.forward_paths.size()))						
						textArea.append("Gain Of Loop = " + signalSolver.loops.get(list.getSelectedIndex() - signalSolver.forward_paths.size()).loop_gain + "\n");
					else if((signalSolver.forward_paths.size() != 0) && list.getSelectedIndex() <= signalSolver.forward_paths.size() - 1){
						textArea.append("Delta Of Path(" + list.getSelectedIndex()+ ") = " + signalSolver.ds.get(list.getSelectedIndex()) + "\n");
						textArea.append("Gain Of Path(" + list.getSelectedIndex()+ ") = " + signalSolver.forward_paths.get(list.getSelectedIndex()).path_gain + "\n");

					}
					
				}
					
			}
		});
		btnPreview.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		//list.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(listScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
						.addComponent(btnPreview, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(listScrollPane, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnPreview, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(13, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		JButton btnAddNode = new JButton("Add Node");
		btnAddNode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				curveApplet.action = -1;
				textArea.append("Add Node\n");
				curveApplet.addNode();
				pathsLoops.clear();
				repaint();		
			}
		});
		
		
		
		btnAddNode.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JButton btnNewButton = new JButton("Add Edge");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(txtWeight.getText() != "" && isNumeric(txtWeight.getText())){
					curveApplet.addEdge(Integer.parseInt(txtWeight.getText()));
					textArea.append(curveApplet.messageToPrint + "\n");
					txtWeight.setText("");
					signIndex = 0;
					pathsLoops.clear();
					repaint();
					
				}
				else
					textArea.append("Enter weight\n");
			}
		});
		
		
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		txtWeight = new JTextField();
		txtWeight.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				
				if(c == '-' && txtWeight.getCaretPosition() == 0 && txtWeight.getText().indexOf('-') == -1){
			    	 
			    }

				else if(c == KeyEvent.VK_BACK_SPACE){

				}
			    else if ((c < '0' || c > '9')) {
			       arg0.consume();
			       getToolkit().beep();
			    }
			    					
			}
		});
		txtWeight.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtWeight.setText("weight");
		txtWeight.setColumns(10);
		
		txtWeight.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
            	txtWeight.setText("");
            	signIndex = 0;
            	curveApplet.action = -1;
            }
        });
		
		
		JButton btnResult = new JButton("Result");
		btnResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(curveApplet.Nodes.size() == 0){
					textArea.append("No Nodes to find path\n");
					return;
				}
				Node[] nodeArray = curveApplet.Nodes.toArray(new Node[curveApplet.Nodes.size()]);
				Edge[] edgeArray = curveApplet.edges.toArray(new Edge[curveApplet.edges.size()]);
				
				signalSolver = new SignalFlowGraph(curveApplet.Nodes.size(), nodeArray, edgeArray);
				textArea.append("Total Gain : " + signalSolver.Mason() + "\n");
				textArea.append("Total Delta : " + signalSolver.Delta + "\n");
				pathsLoops = new DefaultListModel<>();
				list.setModel(pathsLoops);
				
				int k;
				for(k = 0; k < signalSolver.forward_paths.size() ; k++){
					pathsLoops.addElement("Forward Path " + k);
					//textArea.append("Forward Path" + k + " Delta = " + signalSolver.ds.get(k) + "\n");
				}
				
				for(int c = 0; c < signalSolver.loops.size() ; c++){
					pathsLoops.addElement("Loop " + k++);
					//textArea.append("Forward Path" + k + " Delta = " + signalSolver.ds.get(k) + "\n");
				}
				
				for(int c = 0; c < signalSolver.combinations.size() ; c++){
					pathsLoops.addElement("nonTouching " + k++);
					//textArea.append("Forward Path" + k + " Delta = " + signalSolver.ds.get(k) + "\n");
				}
				
				curveApplet.forward_paths = signalSolver.forward_paths;
				curveApplet.loops = signalSolver.loops;
				curveApplet.combinations = signalSolver.combinations;
				
				curveApplet.action = 2;
			}
		});
		
		btnResult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAddNode, GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(txtWeight, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
						.addComponent(btnResult, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(18)
					.addComponent(btnAddNode, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtWeight, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnResult, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
					.addGap(15))
		);
		panel_1.setLayout(gl_panel_1);
		getContentPane().setLayout(groupLayout);
			

	}
	
	//To check numeric values in the textField
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}

class CurveApplet extends JPanel {
	
	private final static int FONTSIZE = 20;
	
	public ArrayList<Node> Nodes = new ArrayList<>();
	public ArrayList<Edge> edges = new ArrayList<>();
	public int pathLoopToPrint;
	
	public ArrayList<Loop> loops;
	public ArrayList<ForwardPath> forward_paths;
	public ArrayList<ArrayList<Loop>> combinations;
	
	private Node tempNode;
	
	private int edgeSelect = -1, tempX, tempY, maxHorSize, maxVerSize;
	private int tempWeight = 1;		// -1 nothing, 1 for addEdge
	
	public int action = -1;
	public String messageToPrint;
	private boolean edgePoint;
	
	CurvePane pane;
	
	
	public CurveApplet(int maxHorSize, int maxVerSize) {
		super(new BorderLayout());
		pane = new CurvePane();
		add(pane, "Center");
		
		MouseHandler handler = new MouseHandler();
		pane.addMouseListener(handler);
		pane.addMouseMotionListener(handler);
		
		this.maxHorSize = maxHorSize;
		this.maxVerSize = maxVerSize;
		
	}
	
	public void addNode(){
		pane.addNode();
	}
	
	public void addEdge(int weight){
		pane.addEdge(weight);
	}

	class CurvePane extends JComponent {
		
		
		public CurvePane() {
			
		}
		
		public void addNode(){
			Nodes.add(new Node(3, 3, Nodes.size()));
		}
		
		public void addEdge(int weight){
			action = 1;
			messageToPrint = "Select And Drag";
			tempWeight = weight;
			
			
		}
		
		private void paintBackground(Graphics2D g2) {
			
			g2.setPaint(Color.LIGHT_GRAY);
			for (int i = 0; i < getSize().width; i += 10) {
				Shape line = new Line2D.Float(i, 0, i, getSize().height);
				g2.draw(line);
			}

			for (int i = 0; i < getSize().height; i += 10) {
				Shape line = new Line2D.Float(0, i, getSize().width, i);
				g2.draw(line);
			}

		}
		
		private void paintPathLoop(Graphics2D g2){
			
			if(pathLoopToPrint >= (forward_paths.size() + loops.size())){
				pathLoopToPrint -= (forward_paths.size() + loops.size());
				
				
					for (int k = 0; k < combinations.get(pathLoopToPrint).size(); k ++) {
						for (int i = 0; i < combinations.get(pathLoopToPrint).get(k).loop.length; i ++) {
							g2.setPaint(Color.yellow);
							Nodes.get(combinations.get(pathLoopToPrint).get(k).loop[i]).draw(g2);
							
							g2.setPaint(Color.blue);
							g2.setFont(new Font("TimesRoman", Font.BOLD, 10));
							g2.drawString(i+"", (int)Nodes.get(combinations.get(pathLoopToPrint).get(k).loop[i]).getCenter().x - 2 , (int) Nodes.get(combinations.get(pathLoopToPrint).get(k).loop[i]).getCenter().y + 5);				
						}	
					}
				
			}
			
			else if(pathLoopToPrint >= forward_paths.size()){
				pathLoopToPrint -= forward_paths.size();
				for (int i = 0; i < loops.get(pathLoopToPrint).loop.length; i ++) {
					g2.setPaint(Color.yellow);
					Nodes.get(loops.get(pathLoopToPrint).loop[i]).draw(g2);
					
					g2.setPaint(Color.blue);
					g2.setFont(new Font("TimesRoman", Font.BOLD, 10));
					g2.drawString(i+"", (int)Nodes.get(loops.get(pathLoopToPrint).loop[i]).getCenter().x - 2 , (int) Nodes.get(loops.get(pathLoopToPrint).loop[i]).getCenter().y + 5);				
				}				
			}
			else{
				for (int i = 0; i < forward_paths.get(pathLoopToPrint).path.length; i ++) {
					g2.setPaint(Color.yellow);
					Nodes.get(forward_paths.get(pathLoopToPrint).path[i]).draw(g2);
					
					g2.setPaint(Color.blue);
					g2.setFont(new Font("TimesRoman", Font.BOLD, 10));
					g2.drawString(i+"", (int)Nodes.get(forward_paths.get(pathLoopToPrint).path[i]).getCenter().x - 2 , (int) Nodes.get(forward_paths.get(pathLoopToPrint).path[i]).getCenter().y + 5);
				
				}
			}
			
			
			
/*			for (int i = 0; i < getSize().height; i += 10) {
				Shape line = new Line2D.Float(0, i, getSize().width, i);
				g2.draw(line);
			}*/
		}
		
		public void paint(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
			paintBackground(g2D);

			
		//Draw edges	
			for(int i = 0 ; i < edges.size() ; i++){
				g2D.setPaint(Color.RED);
				edges.get(i).draw(g2D);
			}
			
		//Draw Control Point	
			g2D.setPaint(Color.RED);
			if(edgeSelect != -1){
				edges.get(edgeSelect).drawCtrlPoint(g2D);
			}
		
		//Draw 
			if(action == 1 && tempNode != null){
				g2D.setPaint(Color.RED);
				g.drawLine((int)tempNode.center.getX(), (int)tempNode.center.getY(), tempX, tempY);
			}
			
		//Draw nodes
			
			for(int i = 0 ; i < Nodes.size() ; i++){
				g2D.setPaint(Color.BLUE);
				
				Nodes.get(i).draw(g2D);
				Nodes.get(i).drawName(g2D);
			}
			
			if(action == 2){
				paintPathLoop(g2D);
				action = -1;
			}
					
		}
		
		
	}



	class MouseHandler extends MouseInputAdapter {
		public void mousePressed(MouseEvent e) {
			edgeSelect = -1;
			
			for(int i = 0 ; i < Nodes.size() ; i++){
				if (Nodes.get(i).contains(e.getX(), e.getY())){
					selected = Nodes.get(i);
					
					if(action == 1){
						 tempNode = Nodes.get(i);
					}
					
					pane.repaint();
					return;
				}
					
			}
			
			for(int i = 0 ; i < edges.size() ; i++){
				if (edges.get(i).ctrlQuad.contains(e.getX(), e.getY())){
					selected = edges.get(i).ctrlQuad;
					edgeSelect = i;
					
					pane.repaint();
					return;
				}
				
			}
			
			for(int i = 0 ; i < edges.size() ; i++){
				if (edges.get(i).contains(e.getX(), e.getY())){
					edgeSelect = i;
					
					pane.repaint();
					return;
				}
					
			}
			
			pane.repaint();
			

		}

		public void mouseReleased(MouseEvent e) {
			edgePoint = false;
			int i;
			
			if(action == 1){
				
				for(i = 0 ; i < Nodes.size() ; i++){
					if (Nodes.get(i).contains(e.getX(), e.getY())){
						edgePoint = true;
						break;
					}		
				}
				if(edgePoint){
					edges.add(new Edge(tempNode, Nodes.get(i), tempWeight));
					edgeSelect = edges.size() - 1;
				}
				action = -1;
				tempNode = null;
				pane.repaint();
			}
			
			selected = null;
			edgePoint = false;
			
		}

		public void mouseDragged(MouseEvent e) {
			tempX = e.getX();
			tempY = e.getY();
			
			if(action == 1){
				pane.repaint();
			}
			else if (selected != null) {
				if (e.getX() < maxHorSize & e.getX() > 0 & e.getY() <maxVerSize & e.getY() > 0) {

					selected.setLocation(e.getX(), e.getY());
					pane.repaint();
				}

			}
		}
		
		public void mouseMoved(MouseEvent e){
			tempX = e.getX();
			tempY = e.getY();
		}
		

		Node selected = null;
	}
}