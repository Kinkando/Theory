package automata;

import static automata.Edge.ARROW_HEAD_SIZE;
import java.awt.*;
import java.awt.geom.Path2D;
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
            
            //triangle of arrow
            double angle = Math.atan2((y - start.y), (x - start.x)) - Math.PI / 2.0;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);

            //point1 of arrow
            double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + x;
            double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + y;
            //point2 of arrow
            double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + x;
            double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + y;

            Path2D path = new Path2D.Double();
            path.moveTo(x, y);
            path.lineTo(x1, y1);
            path.lineTo(x2, y2);
            path.closePath();
            g.fill(path);
            
            g.drawLine((int)start.x, (int)start.y, x, y);
        }
    }
}