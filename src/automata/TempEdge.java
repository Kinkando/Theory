package automata;

import java.awt.*;

public class TempEdge {
    public Vertex source;
    public int x, y;
    
    public TempEdge(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setA(Vertex source) {
        this.source = source;
    }

    public void line(Graphics2D g) {
        g.setColor(Color.RED);
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
        else
            g.drawLine(source.getX(), source.getY(), x, y);
    }
}