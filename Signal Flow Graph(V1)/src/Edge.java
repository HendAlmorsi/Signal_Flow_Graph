import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;



public class Edge {

	private static final int HIT_BOX_SIZE = 2, FONTSIZE = 16;
	private double boxX, boxY, startTempX, startTempY;
	public double weight = 1, x7 = 0, y7 = 0, theta = 0, tempXCalculations, tempYCalculations;
	public boolean circle = false;
	private Ellipse2D.Double circleEdge ;
	private int radius = 10;
	private Path2D.Double arrow ;
	
	public Node start, end, ctrlQuad, tempCtrl;
	Point2D.Double control;
	
	public QuadCurve2D.Double quadCurve;
	Line2D.Double tangent ;
	
	public Edge(Node start, Node end, double weight){
		this(start, end);
		
		this.weight = weight;
	}
	
	
	public Edge(Node start, Node end){
		this.start = start;
		this.end = end;
		
		startTempX = start.getCenter().x;
		startTempY = start.getCenter().y;
		
		arrow = new Path2D.Double();
		
		if(start.center.equals(end.center)){
			circle = true;
			
			ctrlQuad = new Node(start.getCenter().x + radius, start.getCenter().y + radius, true);
			
			circleEdge = new Ellipse2D.Double();
	        circleEdge.setFrameFromCenter(start.getCenter().x + radius, start.getCenter().y + radius, start.getCenter().x + 2*radius, start.getCenter().y + 2*radius);
		}
		
		else{
			ctrlQuad = new Node((start.getCenter().x + end.getCenter().x)/2, (start.getCenter().y + end.getCenter().y)/2);			
			quadCurve = new QuadCurve2D.Double(start.getCenter().x, start.getCenter().y, ctrlQuad.getCenter().x, ctrlQuad.getCenter().y, end.getCenter().x, end.getCenter().y);
		}
		
		
		tempCtrl = new Node(ctrlQuad.getCenter().x, ctrlQuad.getCenter().x);

	}
	
	public void setEdgeWeight(Double newWeight){
		this.weight = newWeight;
	}
	
	public void draw(Graphics2D g2D) {

		if(!circle){
			quadCurve.x1 = start.getCenter().x;
			quadCurve.y1 = start.getCenter().y;

			quadCurve.x2 = end.getCenter().x;
			quadCurve.y2 = end.getCenter().y;

			quadCurve.ctrlx = ctrlQuad.getCenter().x;
			quadCurve.ctrly = ctrlQuad.getCenter().y;
			
			g2D.draw(quadCurve);
			
/*			createArrow((int) ((, (int) ((start.getCenter().y + end.getCenter().y)/2+ctrlQuad.getCenter().y)/2);
			g2D.fill(arrow);*/
			g2D.setFont(new Font("TimesRoman", Font.PLAIN, FONTSIZE));
		     

			theta = Math.atan2(end.getCenter().y - start.getCenter().y, end.getCenter().x - start.getCenter().x);
			x7 = (((start.getCenter().x + end.getCenter().x)/2+ctrlQuad.getCenter().x)/2);
			y7 = ((start.getCenter().y + end.getCenter().y)/2+ctrlQuad.getCenter().y)/2;
			
			g2D.setColor(Color.black);    
			g2D.drawString("" + weight, (int) (((start.getCenter().x + end.getCenter().x)/2+ctrlQuad.getCenter().x)/2),(int) ((start.getCenter().y + end.getCenter().y)/2+ctrlQuad.getCenter().y)/2);
				
		}
		else{
			
			if(ctrlQuad.center.equals(tempCtrl.center)){
				ctrlQuad.setLocation(ctrlQuad.getCenter().x + start.getCenter().x - startTempX, ctrlQuad.getCenter().y + start.getCenter().y - startTempY);
				//radius = (int) Math.sqrt(Math.pow((Math.abs(start.getCenter().x - ctrlQuad.getCenter().x)), 2) + Math.pow(Math.abs(start.getCenter().y - ctrlQuad.getCenter().y), 2)); 

				circleEdge.setFrameFromCenter(ctrlQuad.getCenter().x, ctrlQuad.getCenter().y, ctrlQuad.getCenter().x + radius, ctrlQuad.getCenter().y + radius);
				
				
				//circleEdge.setFrameFromCenter(start.getCenter().x - radius, start.getCenter().y - radius, start.getCenter().x, start.getCenter().y);
				//ctrlQuad.setLocation(start.getCenter().x - radius, start.getCenter().y - radius);
				startTempX = start.getCenter().x;
				startTempY = start.getCenter().y;
			}
			else{
				radius = (int) Math.sqrt(Math.pow((Math.abs(start.getCenter().x - ctrlQuad.getCenter().x)), 2) + Math.pow(Math.abs(start.getCenter().y - ctrlQuad.getCenter().y), 2)); 
				circleEdge.setFrameFromCenter(ctrlQuad.getCenter().x, ctrlQuad.getCenter().y, ctrlQuad.getCenter().x + radius, ctrlQuad.getCenter().y + radius);
				
			}
			
			tempCtrl.setLocation(ctrlQuad.getCenter().x, ctrlQuad.getCenter().y);
			
			g2D.draw(circleEdge);
			tempXCalculations = (start.getCenter().x - ctrlQuad.getCenter().x);
			tempYCalculations = (start.getCenter().y - ctrlQuad.getCenter().y);
/*			createArrow((int)(start.getCenter().x - 2*tempXCalculations), (int)(start.getCenter().x - 2*tempYCalculations));
			g2D.fill(arrow);
			g2D.draw(arrow);*/
				
			
/*			x7 = (start.getCenter().x - 2*tempXCalculations) + 2;
			y7 = (start.getCenter().y - 2*tempYCalculations) + 2;*/
			
			theta = -1*Math.atan2(ctrlQuad.getCenter().y - start.getCenter().y, ctrlQuad.getCenter().x - start.getCenter().x);

  			x7 = (start.getCenter().x - 2.0*tempXCalculations);
  			y7 = (start.getCenter().y - 2.0*tempYCalculations);
			
			//theta = Math.atan2(end.getCenter().y - start.getCenter().y, end.getCenter().x - start.getCenter().x);

			g2D.setColor(Color.black);
			g2D.drawString("" + weight, (int) ((start.getCenter().x - 3*tempXCalculations/2)),(int) ((start.getCenter().y - 3*tempYCalculations/2)));
		
		}
		
		triangle(g2D, x7, y7, theta, 0.5, 8);

	}
	
	public void drawCtrlPoint(Graphics2D g2D) {
		
		if(!circle){
			tangent = new Line2D.Double(start.getCenter(), ctrlQuad.getCenter());
			g2D.draw(tangent);
			tangent = new Line2D.Double(end.getCenter(), ctrlQuad.getCenter());
			g2D.draw(tangent);
		}
		
		ctrlQuad.draw(g2D);
		
		
	}	
	
	public boolean contains(double x, double y) {
//		return quadCurve.getBounds2D().contains(x, y);
		boxX = x - HIT_BOX_SIZE / 2;
		boxY = y - HIT_BOX_SIZE / 2;
		if(!circle)
			return quadCurve.intersects(boxX, boxY, HIT_BOX_SIZE, HIT_BOX_SIZE);
		else{
			return circleEdge.intersects(boxX, boxY, HIT_BOX_SIZE, HIT_BOX_SIZE);
		}
		
	}
	
    public  void triangle(Graphics2D g, double x, double y, double theta, double epsilon, double r) {
		GeneralPath p = new GeneralPath();
		p.moveTo(x, y);
		p.lineTo(x - r * Math.cos(theta + epsilon), y - r * Math.sin(theta + epsilon));
		p.lineTo(x - r * Math.cos(theta - epsilon), y - r * Math.sin(theta - epsilon));
		g.fill(p);
	}

}
