package automata;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SwitchButton extends JPanel implements ActionListener{
    private boolean activated = false, haveBorder = false, haveAnimated = false;
    private Color buttonColor = Color.WHITE;
    private Color activatedColor = Color.GREEN;
    private Color inactivatedColor = Color.RED;
    private int togglePosition, activatedPosition, speed = 5, delay = 20;
    private final int inactivatedPosition, space = 8;
    private final Timer time;
    
    public SwitchButton(boolean haveAnimated, boolean haveBorder) {
        super();
        this.haveBorder = haveBorder;
        this.haveAnimated = haveAnimated;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseClicked(MouseEvent me) {
                if(SwingUtilities.isLeftMouseButton(me)) {
                    activated = !activated;
                    if(haveAnimated)
                        time.start();
                    else
                        repaint();
                }
            }
        });
        inactivatedPosition = space/2+1;
        togglePosition = inactivatedPosition;
        time = new Timer(delay, this);
        time.setInitialDelay(0);
        setVisible(true);
    }
    
    public SwitchButton(boolean haveAnimated) {
        this(haveAnimated, false);
    }
    
    public SwitchButton() {
        this(true, false);
    }
    
    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        resizeActivatedPosition(getWidth()-(getHeight()/2)*2+space/2);
    }
    
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        resizeActivatedPosition(getWidth()-(getHeight()/2)*2+space/2);
    }

    @Override
    public void setBounds(Rectangle rctngl) {
        super.setBounds(rctngl);
        resizeActivatedPosition(getWidth()-(getHeight()/2)*2+space/2);
    }

    @Override
    public void setBounds(int i, int i1, int i2, int i3) {
        super.setBounds(i, i1, i2, i3);
        resizeActivatedPosition(getWidth()-(getHeight()/2)*2+space/2);
    }
    
    public void resizeActivatedPosition(int position) {
        activatedPosition = position;
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

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
        togglePosition = activated ? activatedPosition : inactivatedPosition;
    }

    public Color getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(Color buttonColor) {
        this.buttonColor = buttonColor;
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

    public boolean isHaveBorder() {
        return haveBorder;
    }

    public void setHaveBorder(boolean haveBorder) {
        this.haveBorder = haveBorder;
    }

    public boolean isHaveAnimated() {
        return haveAnimated;
    }

    public void setHaveAnimated(boolean haveAnimated) {
        this.haveAnimated = haveAnimated;
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
    
    @Override
    public void paint(Graphics gr) {
        super.paint(gr);
        final int radius = getHeight()/2;
        final int position = haveAnimated ? togglePosition : (activated ? activatedPosition : inactivatedPosition);
        Graphics2D g = (Graphics2D) gr;
        g.setStroke(new BasicStroke(1));
        
        // Button Body
        g.setColor(activated ? activatedColor : inactivatedColor);
        g.fillRect(radius, 1, getWidth()-radius*2, getHeight()-2);
        g.fillArc(1, 0, radius*2-1, radius*2-1, 270, -180);
        g.fillArc(getWidth()-radius*2, 0, radius*2-1, radius*2-1, 90, -180);
        
        // Button Switch
        g.setColor(buttonColor);
        g.fillOval(position, space/2, radius*2-1-space, radius*2-1-space);
        
        if(haveBorder) {
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
        g.setColor(buttonColor);
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
