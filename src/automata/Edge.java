package automata;

import java.awt.*;
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
            area.setBounds(new QuadCurve2D.Float(inputState.getX(), inputState.getY(), xCenter, yCenter, outputState.getX(), outputState.getY()).getBounds());
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
    
    public void draw(Graphics2D g) {
        g.setFont(Vertex.font);
        g.setColor(selected ? Vertex.highlightColor : Vertex.foregroundColor);
        g.setStroke(new BasicStroke(stroke));
        if(inputState != outputState) {
            // Change loop edge
            g.draw(new QuadCurve2D.Float(inputState.getX(), inputState.getY(), xCenter, yCenter, outputState.getX(), outputState.getY()));
        }
        else{
            double angle = Math.atan2(yCenter - inputState.getY(), xCenter - inputState.getX());
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            int rc = (int)(Vertex.r * Math.sqrt(2));
            int xloop = inputState.getX() - Vertex.r + (int)(dx*rc);
            int yloop = inputState.getY() - Vertex.r + (int)(dy*rc);
            g.drawArc(xloop, yloop, Vertex.r*2, Vertex.r*2, 0, 360); 
        }
        
        // Change line not into vertex area
        
        //ArrowHead: change from inputState.point to pointCenter
        double angle = Math.atan2((outputState.getY() - yCenter), (outputState.getX() - xCenter)) - Math.PI / 2.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        //point1
        double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + outputState.getX();
        double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + outputState.getY();
        //point2
        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE + outputState.getX();
        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE + outputState.getY();
        
//        System.out.println("x: Source: "+inputState.getX()+", Target: "+outputState.getX()+", Center: "+xCenter);
//        System.out.println("y: Source: "+inputState.getY()+", Target: "+outputState.getY()+", Center: "+yCenter);
//        System.out.println("x: "+x1+", "+x2+", "+outputState.getX());
//        System.out.println("y: "+y1+", "+y2+", "+outputState.getY());
        Polygon triangle = new Polygon();
        triangle.addPoint((int)x1, (int)y1);
        triangle.addPoint((int)x2, (int)y2);
        triangle.addPoint(outputState.getX(), outputState.getY());
        g.fillPolygon(triangle);
        
        String text = "";
        for(String c : character)
            text += c+", ";
        text = text.substring(0, text.length()-2);
        characterWidth = g.getFontMetrics(Vertex.font).stringWidth(text);
        characterHeight = g.getFontMetrics(Vertex.font).getHeight();
        g.drawString(text, xCharacter-characterWidth/2, yCharacter);
    }
}
