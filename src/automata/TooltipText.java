package automata;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

public class TooltipText extends JLabel implements MouseMotionListener, MouseListener {
    private Component[] components;
    private final Font tooltipFont;
    private final Color backgroundColor, foregroundColor, borderColor;
    
    public TooltipText() {
        borderColor = Color.BLACK;
        foregroundColor = Color.BLACK;
        backgroundColor = new Color(254,255,208);
        tooltipFont = new Font("TH Sarabun New", Font.PLAIN, 20);
    }

    public void setComponents(Component[] components) {
        this.components = components;
        setFont(tooltipFont);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(borderColor, 1));
        setForeground(foregroundColor);
        setBackground(backgroundColor);
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
            found = x >= cmp.getX() && x<= cmp.getX()+cmp.getWidth() && y>= cmp.getY() && y <= cmp.getY()+cmp.getHeight();
            if(cmp instanceof JButton) {
                ((JButton)cmp).setContentAreaFilled(found && ((JButton)cmp).isEnabled());
            }
            else if(cmp instanceof JToggleButton) {
                ((JToggleButton)cmp).setContentAreaFilled(found || ((JToggleButton)cmp).isSelected());
            }
            if(found) {
                text = cmp.getName();
                break;
            }
        }
        if(!found) {
            setVisible(false);
            return;
        }
        int width = g.getFontMetrics(tooltipFont).stringWidth(text)+10;
        int height = g.getFontMetrics(tooltipFont).getHeight();
        final int positionX = x+width > this.getParent().getWidth() ? x-width : x;
        final int positionY = y+height+10 > this.getParent().getHeight() ? (y+10)-height : y+10;
        setText(text);
        setSize(width, height);
        setLocation(positionX, positionY);
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
