import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Node {
		private static final double radius = 7;
		private static final int FONTSIZE = 16;
		
		public int id = 0;
		private Ellipse2D.Double circle;
		public Point2D.Double center;
		public boolean isCtrl = false;
		
		public Node(double d, double e, int id) {
			
			this(d,e);
			this.id = id;
			
		}
		public Node(double d, double e, boolean isCtrl) {
			this(d,e);
			this.isCtrl = isCtrl;
			
		}
		
		public Node(double d, double e) {
			
			center = new Point2D.Double(d, e);
			circle = new Ellipse2D.Double(center.x - radius, center.y - radius, 2.0 * radius, 2.0 * radius);
		}

		public void draw(Graphics2D g2D) {
			g2D.draw(circle);
			g2D.fill(circle);
		}
		
		public void drawName(Graphics2D g2D) {
			g2D.setFont(new Font("Calibri", Font.ITALIC, FONTSIZE));
		     
			g2D.setColor(Color.black);
		    
			g2D.drawString("node " + this.id, (int) (center.x - 7) , (int) (center.y + 15));
		}

		Point2D.Double getCenter() {
			return center;
		}

		public boolean contains(double x, double y) {
			return circle.contains(x, y);
		}

		public void setLocation(double x, double y) {
			center.x = x;
			center.y = y;
			circle.x = x - radius;
			circle.y = y - radius;
		}


	}