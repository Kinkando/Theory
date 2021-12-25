package automata;

import java.awt.*;
import java.awt.geom.Point2D;

public class TempEdge {
    public Vertex source;
    public int x, y;
    private final Color color = new Color(255, 51, 255);
    
    public TempEdge(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setA(Vertex source) {
        this.source = source;
    }
    
    public Point2D.Double getPoint() {
        double d = Math.sqrt(Math.pow(x-source.getX(), 2) + Math.pow(y-source.getY(), 2));
        double d2 = d - Vertex.r;
        double ratio = d2/d;
        double dx = (x-source.getX()) * ratio;
        double dy = (y-source.getY()) * ratio;
        return new Point.Double(x-dx, y-dy);
    }

    public void line(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        if(source == null)
            return;
        if(source.isInCircle(x, y)){
            double angle = Math.atan2(y - source.getY(), x - source.getX());
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            int rc = (int)(source.r*Math.sqrt(2));
            int xloop = source.getX() - source.r + (int)(dx*rc);
            int yloop = source.getY() - source.r + (int)(dy*rc);
            
            g.drawArc(xloop, yloop , source.r*2, source.r*2, 0, 360); 
        }
        else {
//            Point2D.Double start = new Point2D.Double(source.getX(), source.getY());
            Point2D.Double start = getPoint();
            g.drawLine((int)start.x, (int)start.y, x, y);
        }
    }
}