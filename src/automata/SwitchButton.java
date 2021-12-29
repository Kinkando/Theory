package automata;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SwitchButton extends JPanel implements ActionListener{
    private boolean activated, border , animated, outside;
    private Color switchColor, activatedColor, inactivatedColor;
    private int togglePosition, activatedPosition, inactivatedPosition, speed, delay;
    private final int space;
    private final Timer time;
    
    public SwitchButton() {
        space = 8;
        speed = 5;
        delay = 20;
        activated = false;
        border = false;
        animated = true;
        outside = true;
        switchColor = Color.WHITE;
        activatedColor = Color.GREEN;
        inactivatedColor = Color.RED;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(SwingUtilities.isLeftMouseButton(me)) {
                    activated = !activated;
                    if(animated)
                        time.start();
                    else
                        repaint();
                }
            }
        });
        inactivatedPosition = !outside ? space/2+1 : 0;
        togglePosition = inactivatedPosition;
        time = new Timer(delay, this);
        time.setInitialDelay(0);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(activated && togglePosition < activatedPosition) {
            togglePosition += speed;
            if(togglePosition > activatedPosition)
                togglePosition = activatedPosition;
            repaint();
        }
        else if(!activated && togglePosition > inactivatedPosition) {
            togglePosition -= speed;
            if(togglePosition < inactivatedPosition)
                togglePosition = inactivatedPosition;
            repaint();
        }
        else {
            time.stop();
//            ((Timer)ae.getSource()).stop();
        }
    }
    
    public void setOutside(boolean outside) {
        this.outside = outside;
        resizePosition();
    }
    
    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        resizePosition();
    }
    
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        resizePosition();
    }

    @Override
    public void setBounds(Rectangle rctngl) {
        super.setBounds(rctngl);
        resizePosition();
    }

    @Override
    public void setBounds(int i, int i1, int i2, int i3) {
        super.setBounds(i, i1, i2, i3);
        resizePosition();
    }
    
    public void resizePosition() {
        inactivatedPosition = !outside ? space/2+1 : 0;
        activatedPosition = !outside ? getWidth()-(getHeight()/2)*2+space/2 : getWidth()-getHeight();
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
        togglePosition = activated ? activatedPosition : inactivatedPosition;
    }

    public Color getSwitchColor() {
        return switchColor;
    }

    public void setSwitchColor(Color switchColor) {
        this.switchColor = switchColor;
    }

    public Color getActivatedColor() {
        return activatedColor;
    }

    public void setActivatedColor(Color activatedColor) {
        this.activatedColor = activatedColor;
    }

    public Color getInactivatedColor() {
        return inactivatedColor;
    }

    public void setInactivatedColor(Color inactivatedColor) {
        this.inactivatedColor = inactivatedColor;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean haveBorder() {
        return border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public boolean haveAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }
    
    @Override
    public void paint(Graphics gr) {
        super.paint(gr);
        final int radius = getHeight()/2;
        inactivatedPosition = !outside ? space/2+1 : 0;
        activatedPosition = !outside ? getWidth()-(getHeight()/2)*2+space/2 : getWidth()-getHeight();
        final int position = animated ? togglePosition : (activated ? activatedPosition : inactivatedPosition);
        Graphics2D g = (Graphics2D) gr;
        g.setStroke(new BasicStroke(1));
        
        
        g.setColor(activated ? activatedColor : inactivatedColor);
        
        if(outside) {
            double ratio = 0.75;
            int gap = (int)((1-ratio)/2*getHeight());
            int height = (int)(ratio*getHeight());
            int width = getWidth()-(gap*2+height);
            
            // Button Body
            g.fillRect(gap+height/2, gap+1, width, height-1);
            g.fillArc(gap, gap, height, height, 270, -180);
            g.fillArc(getWidth()-gap-height-1, gap, height, height, 90, -180);

            // Button Switch
            g.setColor(switchColor);
            g.fillOval(position, 0, radius*2, radius*2);
        }
        else {
            // Button Body
            g.fillRect(radius, 1, getWidth()-radius*2, getHeight()-2);
            g.fillArc(1, 0, radius*2-1, radius*2-1, 270, -180);
            g.fillArc(getWidth()-radius*2, 0, radius*2-1, radius*2-1, 90, -180);
    
            // Button Switch
            g.setColor(switchColor);
            g.fillOval(position, space/2, radius*2-1-space, radius*2-1-space);
        }
        
        if(border && !outside) {
            // Button Body Border
            g.setColor(Color.black);
            g.drawLine(radius, 0, radius+getWidth()-radius*2, 0);   // if stroke = 1 then y = 0, otherwise y = 1
            g.drawLine(radius, getHeight()-1, radius+getWidth()-radius*2, getHeight()-1);
            g.drawArc(1, 0, radius*2-1, radius*2-1, 270, -180);
            g.drawArc(getWidth()-radius*2, 0, radius*2-1, radius*2-1, 90, -180);
            
            // Button Switch Border
            g.setColor(Color.black);
            g.drawOval(position, space/2, radius*2-1-space, radius*2-1-space);
        }
        
        /*
        Without space between circle button
        
        super.paint(gr);
        int radius = getHeight()/2;
        int buttonSide = activated ? getWidth()-radius*2 : 1;
        Graphics2D g = (Graphics2D) gr;
        g.setStroke(new BasicStroke(1));
        // Button Body
        g.setColor(activated ? activatedColor : inactivatedColor);
        g.fillRect(radius, 1, getWidth()-radius*2, getHeight()-2);
        g.fillArc(1, 0, radius*2-1, radius*2-1, 270, -180);
        g.fillArc(getWidth()-radius*2, 0, radius*2-1, radius*2-1, 90, -180);
        
        // Button Switch
        g.setColor(switchColor);
        g.fillOval(buttonSide, 0, radius*2-1, radius*2-1);
        
        if(border) {
            // Button Body Border
            g.setColor(Color.black);
            g.drawLine(radius, 1, radius+getWidth()-radius*2, 1);   // if stroke = 1 then y = 0, otherwise y = 1
            g.drawLine(radius, getHeight()-2, radius+getWidth()-radius*2, getHeight()-2);
            g.drawArc(1, 0, radius*2-1, radius*2-1, 270, -180);
            g.drawArc(getWidth()-radius*2, 0, radius*2-1, radius*2-1, 90, -180);
            
            // Button Switch Border
            g.setColor(Color.black);
            g.drawOval(buttonSide, 0, radius*2-1, radius*2-1);
        }
        */
    }
}