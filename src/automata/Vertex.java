package automata;

import java.awt.*;

public class Vertex {
    private int x, y, id, nameWidth, nameHeight;
    private String name;
    private boolean initialState, acceptedState, selected;
    public static int stroke = 2, r = 24, shift = 16;
    public static Color foregroundColor = Color.BLACK, backgroundColor = Color.WHITE, highlightColor = Color.RED;
    public static Font font = new Font("TH Sarabun New", Font.PLAIN, 24);
    
    public Vertex(int x, int y, boolean acceptedState, boolean initialState) {
        this.x = x;
        this.y = y;
        this.initialState = initialState;
        this.acceptedState = acceptedState;
        selected = false;
    }
    
    public Vertex(int x, int y, boolean acceptedState) {
        this(x, y, acceptedState, false);
    }
    
    public Vertex(int x, int y) {
        this(x, y, false, false);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSelected(boolean status) {
        selected = status;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getX() {
        return x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getY() {
        return y;
    }

    public boolean isInitialState() {
        return initialState;
    }

    public void setInitialState(boolean initialState) {
        this.initialState = initialState;
    }

    public boolean isAcceptedState() {
        return acceptedState;
    }

    public void setAcceptedState(boolean acceptedState) {
        this.acceptedState = acceptedState;
    }
    
    public Rectangle getShape() {
        return new Rectangle(x-r, y-r, r*2, r*2);
    }
    
    public boolean isInCircle(int x0, int y0) {
        return ((x0-x)*(x0-x)+(y0-y)*(y0-y)) <= r*r;
    }
    
    public boolean isInCircle(Rectangle selectArea) {
        Rectangle area = getShape();
        return area.x >= selectArea.x && area.y >= selectArea.y &&
               area.x+area.getWidth() <= selectArea.x+selectArea.getWidth() &&
               area.y+area.getHeight() <= selectArea.y+selectArea.getHeight();
    }
    
    public void draw(Graphics2D g) {
        g.setFont(font);
        g.setColor(selected ? highlightColor : foregroundColor);
        g.setStroke(new BasicStroke(stroke));
        g.drawOval(x-r, y-r, r*2, r*2);
        if(initialState) {
            g.drawLine(x-100, y, x-r, y);
            //ArrowHead: change from inputState.point to pointCenter
            double angle = Math.atan2((y - y), ((x-r) - (x-100))) - Math.PI / 2.0;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            //point1
            double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * Edge.ARROW_HEAD_SIZE + (x-r);
            double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * Edge.ARROW_HEAD_SIZE + y;
            //point2
            double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * Edge.ARROW_HEAD_SIZE + (x-r);
            double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * Edge.ARROW_HEAD_SIZE + y;
            
            Polygon triangle = new Polygon();
            triangle.addPoint((int)x1, (int)y1);
            triangle.addPoint((int)x2, (int)y2);
            triangle.addPoint(x-r, y);
            g.fillPolygon(triangle);
        }
        if(acceptedState)
            g.drawOval(x-r+(r-shift)/2, y-r+(r-shift)/2, r*2-(r-shift), r*2-(r-shift));
//        g.fillOval(x-r, y-r, r*2, r*2);
//        g.setColor(backgroundColor);
//        g.fillOval(x-r+(r-shift)/2, y-r+(r-shift)/2, r*2-(r-shift), r*2-(r-shift));   //and change shift = 20
//        g.setColor(foregroundColor);
        nameWidth = g.getFontMetrics(font).stringWidth(name);
        nameHeight = g.getFontMetrics(font).getHeight();
        g.drawString(name, x-nameWidth/2 , y+nameHeight/4-5);
    }
}
