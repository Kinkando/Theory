package automata;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TooltipText extends JLabel implements MouseMotionListener, MouseListener {
    private Component[] components;

    public void setComponents(Component[] components) {
        this.components = components;
        setFont(Vertex.font);
        setText("");
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setBackground(new Color(254,255,208));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setSize(new Dimension(100,100));
        setVisible(false);
    }
    
    public void close() {
        setVisible(false);
    }
    
    private void display(final int x, final int y) {
        BufferedImage image = new BufferedImage(900, 900, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        String text = "";
        boolean found = false;
        for(Component cmp : components) {
            if(x >= cmp.getX() && x<= cmp.getX()+cmp.getWidth() && y>= cmp.getY() && y <= cmp.getY()+cmp.getHeight()) {
                text = cmp.getName();
                found = true;
                break;
            }
        }
        if(!found) {
            setVisible(false);
            return;
        }
        int width = g.getFontMetrics(Vertex.font).stringWidth(text);
        int height = g.getFontMetrics(Vertex.font).getHeight();
        setText(text);
        setSize(width+10, height);
        setLocation(x+5, y+5);
        setVisible(true);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        final int x = me.getX();
        final int y = me.getY();
        display(x, y);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        
    }

    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        final int x = me.getX();
        final int y = me.getY();
        display(x, y);
    }

    @Override
    public void mouseExited(MouseEvent me) {
        final int x = me.getX();
        final int y = me.getY();
        display(x, y);
    }
    
}
