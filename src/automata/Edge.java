package automata;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;

public class Edge {
    public static final int ARROW_HEAD_SIZE = 15;
    public static int stroke = 2;
    private Vertex inputState, outputState;
    private String[] character;
    private boolean selected;
    private int characterWidth, characterHeight, xCenter, yCenter, rCenter, xCharacter, yCharacter;
    
    public Edge(Vertex source, Vertex target) {
        inputState = source;
        outputState = target;
        rCenter = 50;
        selected = false;
    }
    
    public Edge(Vertex source, Vertex target, String text) {
        this(source, target);
        character = new String[1];
        character[0] = text;
    }
    
    public Edge(Vertex source, Vertex target, String[] text) {
        this(source, target);
        character = text;
    }
    
    public void setInputState(Vertex inputState) {
        this.inputState = inputState;
    }
    
    public Vertex getInputState() {
        return inputState;
    }
    
    public void setOutputState(Vertex outputState) {
        this.outputState = outputState;
    }
    
    public Vertex getOutputState() {
        return outputState;
    }
    
    public void setCharacter(String[] character) {
        this.character = character;
    }
    
    public String[] getCharacter() {
        return character;
    }
    
    public void setSelected(boolean status) {
        selected = status;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public int getXCenter() {
        return xCenter;
    }
    
    public void setXCenter(int x_center) {
        this.xCenter = x_center;
    }
    
    public int getYCenter() {
        return yCenter;
    }
    
    public void setYCenter(int y_center) {
        this.yCenter = y_center;
    }

    public int getRCenter() {
        return rCenter;
    }

    public void setRCenter(int r_center) {
        this.rCenter = r_center;
    }

    public int getXCharacter() {
        return xCharacter;
    }

    public void setXCharacter(int x_character) {
        this.xCharacter = x_character;
    }

    public int getYCharacter() {
        return yCharacter;
    }

    public void setYCharacter(int y_character) {
        this.yCharacter = y_character;
    }
    
    public String getString() {
        String str = "";
        for(String cha : character)
            str += cha+", ";
        return str.substring(0, str.length()-2);
    }
    
    public Rectangle getShape() {
        Rectangle area = new Rectangle();
        if(inputState != outputState) {
            Point.Double start = getPoint("start");
            Point.Double end = getPoint("end");
            area.setBounds(new QuadCurve2D.Double(start.x, start.y, xCenter, yCenter, end.x, end.y).getBounds());
        }
        else{
            double angle = Math.atan2(yCenter - inputState.getY(), xCenter - inputState.getX());
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            int rc = (int)(Vertex.r * Math.sqrt(2));
            int xloop = inputState.getX() - Vertex.r + (int)(dx*rc);
            int yloop = inputState.getY() - Vertex.r + (int)(dy*rc);
            area.setBounds(xloop, yloop, Vertex.r*2, Vertex.r*2);
        }
        return area;
    }
    
    public boolean isInLine(int x, int y) {
        return ((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter)) <= rCenter * rCenter ;
    }
    
    public boolean isInLine(Rectangle selectArea) {
        Rectangle area = getShape();
        return area.x >= selectArea.x && area.y >= selectArea.y &&
               area.x+area.getWidth() <= selectArea.x+selectArea.getWidth() &&
               area.y+area.getHeight() <= selectArea.y+selectArea.getHeight();
    }
    
    public Point.Double getPoint(String side) {
        // diff between inputState and outputState
        double d = Math.sqrt(Math.pow(outputState.getX()-inputState.getX(), 2) + Math.pow(outputState.getY()-inputState.getY(), 2));
        double d2 = d - Vertex.r;
        double ratio = d2/d;
        double dx = (outputState.getX()-inputState.getX()) * ratio;
        double dy = (outputState.getY()-inputState.getY()) * ratio;
        if(side.equalsIgnoreCase("start"))
            return new Point.Double(outputState.getX()-dx, outputState.getY()-dy);
        return new Point.Double(inputState.getX()+dx, inputState.getY()+dy);
    }
    
    public Point.Double getCenter(Point.Double start, Point.Double end) {
        double cX = xCenter + (xCenter-(end.x+start.x)/2);
        double cY = yCenter + (yCenter-(end.y+start.y)/2);
//        double cX = xCenter;
//        double cY = yCenter;
        return new Point.Double(cX, cY);
    }
    
    public void draw(Graphics2D g) {
        g.setFont(Vertex.font);
        g.setColor(selected ? Vertex.highlightColor : Vertex.foregroundColor);
        g.setStroke(new BasicStroke(stroke));
        
        final Point.Double start = getPoint("start");
        final Point.Double end = getPoint("end");
//        final Point.Double start = new Point.Double(inputState.getX(), inputState.getY());
//        final Point.Double end = new Point.Double(outputState.getX(), outputState.getY());
        final Point.Double center = getCenter(start, end);
        
        //triangle of arrow
        double angle = Math.atan2((end.y - center.y), (end.x - center.x)) - Math.PI / 2.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        //point1 of arrow
        double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + end.x;
        double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + end.y;
        //point2 of arrow
        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + end.x;
        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + end.y;
        
        Path2D path = new Path2D.Double();
        path.moveTo(end.x, end.y);
        path.lineTo(x1, y1);
        path.lineTo(x2, y2);
        path.closePath();
        g.fill(path);
        
        if(inputState != outputState) {
            g.draw(new QuadCurve2D.Double(start.x, start.y, center.x, center.y, end.x, end.y));
        }
        else{
            angle = Math.atan2(yCenter - inputState.getY(), xCenter - inputState.getX());
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            int rc = (int)(Vertex.r * Math.sqrt(2));
            int xloop = inputState.getX() - Vertex.r + (int)(dx*rc);
            int yloop = inputState.getY() - Vertex.r + (int)(dy*rc);
            g.drawArc(xloop, yloop, Vertex.r*2, Vertex.r*2, 0, 360); 
        }
        
        String text = "";
        for(String c : character)
            text += c+", ";
        text = text.substring(0, text.length()-2);
        characterWidth = g.getFontMetrics(Vertex.font).stringWidth(text);
        characterHeight = g.getFontMetrics(Vertex.font).getHeight();
        g.drawString(text, xCharacter-characterWidth/2, yCharacter);
    }
}
