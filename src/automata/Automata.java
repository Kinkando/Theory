package automata;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class Automata implements MouseListener, MouseMotionListener, KeyListener {
    
    private final JFrame frame;
    private final JPanel drawingPanel, toolbarPanel;
    private final JButton openButton, saveButton, undoButton, redoButton, clearButton, inputAlphabetButton, manualInputButton;
    private final SwitchButton themeButton;
    private final JToggleButton createVertexButton, createEdgeButton;
    private final JInternalFrame detailFrame;
    private final StateHandle stateHandle;
    private static final int GAPS_X = 10, GAPS_Y = 10;
    public ArrayList<Vertex> vertexs;
    public ArrayList<Edge> edges;
    public String[] inputAlphabet;
    private TempEdge tempEdge;
    private Object selected;
    private final TooltipText tooltipText;
    
    // Component in JInternalFrame (detailFrame)
    private final JLabel subLabel1, subLabel2, subLabel3;
    private final JTextField subField1, subField2, subField3;
    private final SwitchButton subSwitch2, subSwitch3;
    
    public Automata() {
        frame = new JFrame("Automata");
        detailFrame = new JInternalFrame("", false, true, false, false);
        toolbarPanel = new JPanel();
        drawingPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                edges.forEach((e) -> {
                    e.draw(g2d);
                });
                vertexs.forEach((v) -> {
                    v.draw(g2d);
                });
                if(tempEdge!=null)
                    tempEdge.line(g2d);
                drawingPanel.requestFocusInWindow();
            }
        };
        openButton = new JButton("Open");
        saveButton = new JButton("Save");
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");
        clearButton = new JButton("Clear");
        themeButton = new SwitchButton();
        createVertexButton = new JToggleButton("Create Vertex");
        createEdgeButton = new JToggleButton("Create Edge");
        inputAlphabetButton = new JButton("Input Set");
        manualInputButton = new JButton("Manual Input");
        vertexs = new ArrayList<>();
        edges = new ArrayList<>();
        inputAlphabet = new String[1];
        inputAlphabet[0] = "a";
        stateHandle = new StateHandle();
        tooltipText = new TooltipText();
        
        subLabel1 = new JLabel();
        subLabel2 = new JLabel();
        subLabel3 = new JLabel();
        subField1 = new JTextField();
        subField2 = new JTextField();
        subField3 = new JTextField();
        subSwitch2 = new SwitchButton();
        subSwitch3 = new SwitchButton();
    }
    
    public void setLayout() {
        GroupLayout toolbarPanelLayout = new GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 1080, Short.MAX_VALUE)
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
        );
        GroupLayout drawingPanelLayout = new GroupLayout(drawingPanel);
        drawingPanel.setLayout(drawingPanelLayout);
        drawingPanelLayout.setHorizontalGroup(
            drawingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(toolbarPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        drawingPanelLayout.setVerticalGroup(
            drawingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(drawingPanelLayout.createSequentialGroup()
                .addComponent(toolbarPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 659, Short.MAX_VALUE))
        );
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(drawingPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(drawingPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        int lastButtonIndex = toolbarPanel.getComponents().length-1;
        Component lastChild = toolbarPanel.getComponent(lastButtonIndex);
        frame.setMinimumSize(new Dimension(lastChild.getX()+lastChild.getWidth()+GAPS_X+20, 480));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public void setGUI() {
        // Remove system menu in JInternalFrame (drop down array on top left)
        BasicInternalFrameUI ui = (BasicInternalFrameUI)detailFrame.getUI();
        Container north = (Container)ui.getNorthPane();
        north.remove(0);
        north.validate();
        north.repaint();
        
        detailFrame.setLayout(null);
        detailFrame.getContentPane().setBackground(Vertex.backgroundColor);
        detailFrame.add(subLabel1);
        detailFrame.add(subLabel2);
        detailFrame.add(subLabel3);
        final int labelLength = detailFrame.getContentPane().getComponentCount();
        
        Component previousComponent = null;
        for(Component c : detailFrame.getContentPane().getComponents()) {
            JLabel label = (JLabel) c;
            label.setFont(Vertex.font);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setSize(120, 20);
            label.setLocation(20, previousComponent == null ? 20 : previousComponent.getY()+previousComponent.getHeight()+20);
            previousComponent = c;
        }
        
        detailFrame.setBackground(Vertex.backgroundColor);
        detailFrame.setSize(400, previousComponent.getY()+previousComponent.getHeight()+60);
        detailFrame.add(subField1);
        detailFrame.add(subField2);
        detailFrame.add(subSwitch2);
        detailFrame.add(subField3);
        detailFrame.add(subSwitch3);
        
        subField1.setFont(Vertex.font);
        subField1.setSize(200, 30);
        subField1.setLocation(160, 15);
        
        previousComponent = subField1;
        for(int i=labelLength+1;i<detailFrame.getContentPane().getComponentCount(); i+=2) {
            JTextField textField = (JTextField) detailFrame.getContentPane().getComponent(i);
            textField.setFont(Vertex.font);
            textField.setSize(200, 30);
            textField.setLocation(160, previousComponent.getY()+previousComponent.getHeight()+10);
            textField.setVisible(false);
            
            SwitchButton switchButton = (SwitchButton) detailFrame.getContentPane().getComponent(i+1);
            switchButton.setBackground(detailFrame.getBackground());
            switchButton.setInactivatedColor(new Color(190, 190, 190));
            switchButton.setSize(60, 30);
            switchButton.setLocation(160, previousComponent.getY()+previousComponent.getHeight()+10);
            switchButton.setVisible(false);

            previousComponent = textField;
        }
        
        subField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    changeStateName(subField1.getText());
                }
            }
        });
        subField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    changeInputCharacter(subField2.getText());
                }
            }
        });
        
        subSwitch2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                if(SwingUtilities.isLeftMouseButton(me)) {
                    initialStateSwitch();
                }
            }
        });
        subSwitch3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                if(SwingUtilities.isLeftMouseButton(me)) {
                    acceptedStateSwitch();
                }
            }
        });
        
        toolbarPanel.setBackground(new Color(255, 255, 204));
        toolbarPanel.add(openButton);
        toolbarPanel.add(saveButton);
        toolbarPanel.add(undoButton);
        toolbarPanel.add(redoButton);
        toolbarPanel.add(clearButton);
        toolbarPanel.add(createVertexButton);
        toolbarPanel.add(createEdgeButton);
        toolbarPanel.add(inputAlphabetButton);
        toolbarPanel.add(manualInputButton);
        toolbarPanel.add(themeButton);
        toolbarPanel.addMouseListener(tooltipText);
        toolbarPanel.addMouseMotionListener(tooltipText);
        
        previousComponent = null;
        for(Component c : toolbarPanel.getComponents()) {
            if(c instanceof SwitchButton) {
                SwitchButton switchButton = (SwitchButton) c;
                switchButton.setSize(80, 40);
                switchButton.setBackground(toolbarPanel.getBackground());
                switchButton.setActivated(true);
                switchButton.setActivatedColor(Color.ORANGE);
                switchButton.setInactivatedColor(Color.BLACK);
                switchButton.setLocation((previousComponent != null ? previousComponent.getX() + previousComponent.getWidth() : 0) + GAPS_X, GAPS_Y);
                switchButton.setName("Dark Theme");
            }
            else {
                c.setFont(Vertex.font);
                c.setFocusable(false);
                c.setSize(140, 40);
                c.setLocation((previousComponent != null ? previousComponent.getX() + previousComponent.getWidth() : 0) + GAPS_X, GAPS_Y);
                //
                if(c instanceof JButton)
                    c.setName(((JButton)c).getText());
                else if(c instanceof JToggleButton)
                    c.setName(((JToggleButton)c).getText());
                //
            }
            previousComponent = c;
        }
        tooltipText.setComponents(toolbarPanel.getComponents());
        
        openButton.addActionListener(this::openAction);
        saveButton.addActionListener(this::saveAction);
        undoButton.addActionListener(this::undoAction);
        redoButton.addActionListener(this::redoAction);
        clearButton.addActionListener(this::clearAction);
        createVertexButton.addActionListener(this::createVertexAction);
        createEdgeButton.addActionListener(this::createEdgeAction);
        inputAlphabetButton.addActionListener(this::inputAlphabetAction);
        manualInputButton.addActionListener(this::manualInputAction);
        themeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                if(SwingUtilities.isLeftMouseButton(me)) {
                    themeAction();
                }
            }
        });
        
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        clearButton.setEnabled(false);
        
        drawingPanel.setFocusable(true);
        drawingPanel.setBackground(Vertex.backgroundColor);
        drawingPanel.addMouseListener(this);
        drawingPanel.addMouseMotionListener(this);
        drawingPanel.addKeyListener(this);
        drawingPanel.add(detailFrame);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(toolbarPanel);
        frame.add(drawingPanel);
        frame.getLayeredPane().add(tooltipText,JLayeredPane.PALETTE_LAYER);
    }
    
    private void setEnabledButton() {
        undoButton.setEnabled(stateHandle.undoIsEnabled());
        redoButton.setEnabled(stateHandle.redoIsEnabled());
        clearButton.setEnabled(!vertexs.isEmpty() || !edges.isEmpty());
    }
    
    private void openAction(ActionEvent ae) {
        tooltipText.close();
        
        
        
    }
    
    private void saveAction(ActionEvent ae) {
        tooltipText.close();
        
        
        
    }
    
    private void undoAction(ActionEvent ae) {
        if(!stateHandle.undoIsEnabled())
            return;
        stateHandle.undo(vertexs, edges);
        drawingPanel.repaint();
        setEnabledButton();
        stateHandle.changeHandle(vertexs, edges);
        detailFrame.setVisible(false);
        selected = null;
    }
    
    private void redoAction(ActionEvent ae) {
        if(!stateHandle.redoIsEnabled())
            return;
        stateHandle.redo(vertexs, edges);
        drawingPanel.repaint();
        setEnabledButton();
        stateHandle.changeHandle(vertexs, edges);
        detailFrame.setVisible(false);
        selected = null;
    }
    
    private void clearAction(ActionEvent ae) {
        if(vertexs.isEmpty() && edges.isEmpty())
            return;
        edges.clear();
        vertexs.clear();
        stateHandle.changeHandle(vertexs, edges);
        drawingPanel.repaint();
        clearButton.setEnabled(false);
        redoButton.setEnabled(false);
        detailFrame.setVisible(false);
    }
    
    private void createVertexAction(ActionEvent ae) {
        if(createEdgeButton.isSelected())
            createEdgeButton.setSelected(false);
    }
    
    private void createEdgeAction(ActionEvent ae) {
        if(createVertexButton.isSelected())
            createVertexButton.setSelected(false);
    }
    
    private void manualInputAction(ActionEvent ae) {
        tooltipText.close();
        /*
            Manual input such as Universe character input set, initial state, accepted state set, function
        */
    }
    
    private void themeAction() {
        tooltipText.close();
        Color temp = Vertex.backgroundColor;
        Vertex.backgroundColor = Vertex.foregroundColor;
        Vertex.foregroundColor = temp;
        themeButton.setName(!themeButton.isActivated() ? "Light Theme" : "Dark Theme");
        
        detailFrame.getContentPane().setBackground(Vertex.backgroundColor);
        subLabel1.setForeground(Vertex.foregroundColor);
        subLabel2.setForeground(Vertex.foregroundColor);
        subLabel3.setForeground(Vertex.foregroundColor);
        subSwitch2.setBackground(Vertex.backgroundColor);
        subSwitch3.setBackground(Vertex.backgroundColor);
        
        drawingPanel.setBackground(Vertex.backgroundColor);
    }
    
    private void inputAlphabetAction(ActionEvent ae) {
        tooltipText.close();
        String currentInputSet = "";
        for(String str : inputAlphabet) 
            currentInputSet += str+", ";
        currentInputSet = currentInputSet.substring(0, currentInputSet.length()-2);
        String newInputSet = JOptionPane.showInputDialog(frame.getContentPane(), "Input alphabet set", currentInputSet);
        if(newInputSet == null)
            return;
        inputAlphabetHandle(newInputSet);
        
        // When delete or change input alphabet set, what happened ? => request to change name or delete ?
        
        // Can redo or undo ?
    }
    
    private boolean inputAlphabetChecking(String inputSet, boolean isUniverseInputSet) {
        String[] inputSets = inputSet.split(",");
        boolean check = false;
        ArrayList<String> inputToken = new ArrayList<>();
        for(String input : inputSets) {
            input = input.trim();
            if(input.isEmpty()) {
                showErrorMaterialDialog("Error", "Input alphabet must not empty");
                return false;
            }
            check = true;
            for(int i=0;i<input.length();i++) {
                if(!Character.isAlphabetic(input.charAt(i))) {
                    showErrorMaterialDialog("Error", "Input alphabet only alphabetic");
                    return false;
                }
            }
            
            for(String token : inputToken)
                if(token.equals(input)) {
                    showErrorMaterialDialog("Error", "Input alphabet must not be repeat");
                    return false;
                }
            inputToken.add(input);
            
            if(!isUniverseInputSet) {
                boolean found = false;
                for(String universe : inputAlphabet) {
                    if(universe.equals(input)) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    showErrorMaterialDialog("Error", "Some input alphabet not exist in input character set");
                    return false;
                }
            }
        }
        if(!check)
            showErrorMaterialDialog("Error", "Input alphabet must not be empty set");
        return check;
    }
    
    private void inputAlphabetHandle(String inputSet) {
        if(inputAlphabetChecking(inputSet, true)) {
            String[] inputSets = inputSet.split(",");
            int setLength = 0;
            for(String input : inputSets) {
                input = input.trim();
                if(!input.isEmpty())
                    setLength++;
            }
            inputAlphabet = new String[setLength];
            for(int i=0;i<setLength;i++) 
                inputAlphabet[i] = inputSets[i].trim();
        }
    }
    
    private void changeStateName(String newStateName) {
        if(selected == null || !(selected instanceof Vertex))
            return;
        Vertex v = (Vertex) selected;
        String currentStateName = v.getName();
        if(currentStateName.equals(newStateName))
            return;
        for(Vertex vertex : vertexs) {
            if(vertex.getName().equals(newStateName)) {
                showErrorMaterialDialog("Error", "State name must not repeat");
                subField1.setText(currentStateName);
                return;
            }
        }
        subField1.setText(newStateName);
        v.setName(newStateName);
        drawingPanel.repaint();
        stateHandle.changeHandle(vertexs, edges);
    }
    
    private void changeInputCharacter(String newInputCharacter) {
        if(selected == null || !(selected instanceof Edge))
            return;
        Edge e = (Edge) selected;
        String currentInputCharacter = e.getString();
        if(currentInputCharacter.equals(newInputCharacter))
            return;
        if(inputAlphabetChecking(newInputCharacter, false)) {
            subField2.setText(newInputCharacter);
            
            String[] inputSets = new String[newInputCharacter.length()];
            int count = 0;
            for(String input : newInputCharacter.split(",")) {
                if(!input.trim().isEmpty())
                    inputSets[count++] = input.trim();
            }
            
            String[] notEmptyExistInputSet = new String[count];
            for(int i=0;i<count;i++)
                notEmptyExistInputSet[i] = inputSets[i];
            
            e.setCharacter(notEmptyExistInputSet);
            drawingPanel.repaint();
            stateHandle.changeHandle(vertexs, edges);
        }
        else 
            subField2.setText(currentInputCharacter);
    }
    
    private void showErrorMaterialDialog(String title, String content) {
        JOptionPane.showMessageDialog(frame.getContentPane(), content, title, JOptionPane.ERROR_MESSAGE);
    }
    
    private void initialStateSwitch() {
        if(selected == null || !(selected instanceof Vertex))
            return;
        Vertex v = (Vertex) selected;
        v.setInitialState(!v.isInitialState());
        for(Vertex vertex : vertexs) {
            if(vertex != v)
                vertex.setInitialState(false);
        }
        drawingPanel.repaint();
        stateHandle.changeHandle(vertexs, edges);
        setEnabledButton();
    }
    
    private void acceptedStateSwitch() {
        if(selected == null || !(selected instanceof Vertex))
            return;
        Vertex v = (Vertex) selected;
        v.setAcceptedState(!v.isAcceptedState());
        drawingPanel.repaint();
        stateHandle.changeHandle(vertexs, edges);
        setEnabledButton();
    }
    
    private void displayDetailFrame() {
        if(selected != null) {
            Rectangle objectArea = new Rectangle();
            if (selected instanceof Vertex) {
                Vertex v = (Vertex) selected;
                detailFrame.setTitle("Vertex detail");
                subLabel1.setText("State");
                subLabel2.setText("Initial State");
                subLabel3.setText("Accepted State");
                subField1.setText(v.getName());
                subSwitch2.setActivated(v.isInitialState());
                subSwitch3.setActivated(v.isAcceptedState());
                objectArea.setBounds(v.getX()-Vertex.r, v.getY()-Vertex.r, Vertex.r*2, Vertex.r*2);
            } 
            else {
                Edge e = (Edge) selected;
                detailFrame.setTitle("Edge detail");
                subLabel1.setText("Input State");
                subLabel2.setText("Input Character");
                subLabel3.setText("Output State");
                subField1.setText(e.getInputState().getName());
                String inputCharacters = "";
                for(String c : e.getCharacter())
                    inputCharacters += c+", ";
                inputCharacters = inputCharacters.substring(0, inputCharacters.length()-2);
                subField2.setText(inputCharacters);
                subField3.setText(e.getOutputState().getName());
                subField3.setEditable(false);
                objectArea.setFrameFromDiagonal(e.getInputState().getX(), e.getInputState().getY(), e.getOutputState().getX(), e.getOutputState().getY());
            }
            subField1.setEditable(selected instanceof Vertex);
            subSwitch2.setVisible(selected instanceof Vertex);
            subSwitch3.setVisible(selected instanceof Vertex);
            subField2.setVisible(selected instanceof Edge);
            subField3.setVisible(selected instanceof Edge);
            
            Rectangle frameBox = new Rectangle(40, toolbarPanel.getHeight()+40, detailFrame.getWidth(), detailFrame.getHeight());
            detailFrame.setLocation(frameBox.intersects(objectArea) ? frame.getMinimumSize().width-detailFrame.getWidth()-60 : 40, toolbarPanel.getHeight()+40);
            detailFrame.setVisible(true);
            
            
            // if change state name, it will change size of circle that depend on font metric width
            // and when create vertex that above 100 the same.
            
            
        }
        else {
            detailFrame.dispose();
        }
    }
    
    public void selected(int x, int y) {
        deselected();
        Object tempSelected = null;
        for(Vertex v : vertexs) {
            if(v.isInCircle(x, y)) {
                v.setSelected(true);
                tempSelected = v;
                break;
            }
        }
        if(tempSelected == null) {
            for(Edge e : edges) {
                if(e.isInLine(x, y)) {
                    e.setSelected(true);
                    tempSelected = e;
                    break;
                }
            }
        }
        // Cancel current highlight object
        if(selected != null && selected != tempSelected) {
            if (selected instanceof Vertex) {
                Vertex v = (Vertex) selected;
                v.setSelected(false);
            } 
            else {
                Edge e = (Edge) selected;
                e.setSelected(false);
            }
        }
        selected = tempSelected;
        displayDetailFrame();
        drawingPanel.repaint();
    }
    
    private void deselected() {
        vertexs.forEach((v) -> {
            v.setSelected(false);
        });
        edges.forEach((e) -> {
            e.setSelected(false);
        });
    }
    
    private void createVertex(int x, int y) {
        boolean check = false;
        for(Vertex v : vertexs) {
            if(v.isInCircle(x, y)) {
                check = true;
                break;
            }
        }
        if(!check) {
            Vertex vertex = new Vertex(x, y, false, vertexs.isEmpty());
            
            // Check repeat state name and auto generate
            ArrayList<Vertex> temp = new ArrayList<>();
            String name = "q0";
            boolean found = false;
            if(!vertexs.isEmpty()) {
                vertexs.forEach((v) -> {
                    temp.add(v);
                });
                for(int i=0;i<vertexs.size();i++) {
                    boolean checkRepeat = false;
                    for(Vertex v : temp) {
                        if(v.getName().equals("q"+(i+1))) {
                            temp.remove(v);
                            checkRepeat = true;
                            break;
                        }
                    }
                    if(!checkRepeat) {
                        found = true;
                        name = "q"+(i+1);
                        break;
                    }
                }
                vertex.setName(found ? name : "q"+(vertexs.size()+1));
            }
            else
                vertex.setName(name);
            
            deselected();
            vertex.setSelected(true);
            selected = vertex;
            displayDetailFrame();
            
            vertexs.add(vertex);
            drawingPanel.repaint();
        }
    }
    
    private void createEdge(int x, int y) {
        // Create new edge with source vertex
        if(tempEdge == null) {
            for(Vertex v : vertexs) {
                if(v.isInCircle(x, y)) {
                    tempEdge = new TempEdge(x, y);
                    tempEdge.setA(v);
                    break;
                }
            }
        }
        // Create complete edge with source and target vertex
        else if(tempEdge != null) {

            // Check repeat state

            // Change position to around circle (not center but border)

            Vertex target = null;
            for (Vertex t : vertexs) {
                if (t.isInCircle(x, y)) {
                    tempEdge.x = t.getX();
                    tempEdge.y = t.getY();
                    target = t;
                    
                    /*
                        Can create same edge
                        if same and remain incomplete yet, it will merge edge (add input)
                        otherwise it can't create
                    */
                    
                    Edge edge = new Edge(tempEdge.source, target, inputAlphabet[0]);
                    // source is not the same as target
                    int xCenter = (tempEdge.source.getX() + target.getX())/2;   
                    int yCenter = (tempEdge.source.getY() + target.getY())/2;

                    if(target == tempEdge.source) {
                        double angle = Math.atan2(y - tempEdge.source.getY(), x - tempEdge.source.getX());
                        double dx = Math.cos(angle);
                        double dy = Math.sin(angle);
                        int rc = 3 * Vertex.r;
                        xCenter = tempEdge.source.getX() + (int)(dx*rc);
                        yCenter = tempEdge.source.getY() + (int)(dy*rc);
                    }
                    edge.setXCenter(xCenter);
                    edge.setYCenter(yCenter);
                    edge.setXCharacter(xCenter);
                    edge.setYCharacter(yCenter);
                    
                    deselected();
                    edge.setSelected(true);
                    selected = edge;
                    displayDetailFrame();

                    edges.add(edge);
                    break;
                }
            }
            tempEdge = null; 
        }
        drawingPanel.repaint();
    }
    
    private boolean cornerPosition(int x, int y) {
        return x <= Vertex.r || x >= frame.getWidth()-Vertex.r*2 || y <= toolbarPanel.getHeight()+Vertex.r || y >= frame.getHeight()-Vertex.r*3;
    }

    //MouseListener
    @Override
    public void mouseClicked(MouseEvent me) {
        final int x = me.getX();
        final int y = me.getY();
        if(cornerPosition(x, y))
            return;
        if(SwingUtilities.isRightMouseButton(me)) { // Cancel drawing
            selected = null;
            tempEdge = null;
            drawingPanel.repaint();
        }
        else if(SwingUtilities.isLeftMouseButton(me)) {
            if(createVertexButton.isSelected()) {
                createVertex(x, y);
            }
            else if(createEdgeButton.isSelected()) {
                createEdge(x, y);
            }
            else {
                selected(x, y);
                return;
            }
        }
        stateHandle.changeHandle(vertexs, edges);
        setEnabledButton();
        
        // Multiply item selected ?
    }

    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        stateHandle.changeHandle(vertexs, edges);
        setEnabledButton();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }

    //MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent me) {
        final int x = me.getX();
        final int y = me.getY();
        if(cornerPosition(x, y))
            return;
        if (selected != null && !createVertexButton.isSelected() && !createEdgeButton.isSelected()) {
            if (selected instanceof Vertex && SwingUtilities.isLeftMouseButton(me)) {
                Vertex s = (Vertex) selected;
                for (Edge e : edges) {
                    if (e.getInputState() == s || e.getOutputState() == s) {
                        int difx = x - s.getX();
                        int dify = y - s.getY();
                        int xCenter = (e.getInputState().getX() + e.getOutputState().getX()) / 2;
                        int yCenter = (e.getInputState().getY() + e.getOutputState().getY()) / 2;
                        if (e.getInputState() == e.getOutputState()) {
                            xCenter = e.getXCenter() + difx;
                            yCenter = e.getYCenter() + dify;
                        }
                        e.setXCenter(xCenter);
                        e.setYCenter(yCenter);
                        e.setXCharacter(xCenter);
                        e.setYCharacter(yCenter);
                    }
                }
                s.setX(x);
                s.setY(y);
            } 
            else {
                if(SwingUtilities.isLeftMouseButton(me)) {
                    Edge e = (Edge) selected;
                    e.setXCenter(x);
                    e.setYCenter(y);
                    e.setXCharacter(x);
                    e.setYCharacter(y);
                }
                else if(SwingUtilities.isRightMouseButton(me)) {
                    Edge e = (Edge) selected;
                    e.setXCharacter(x);
                    e.setYCharacter(y+10);
                    frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
            
            // Check collision of Vertex and Edge
            
            drawingPanel.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        final int x = me.getX();
        final int y = me.getY();
        if(createEdgeButton.isSelected() && tempEdge != null) {
            tempEdge.x = x;
            tempEdge.y = y;
            drawingPanel.repaint();
        }
    }
    
    //KeyListener
    @Override
    public void keyTyped(KeyEvent ke) {
        int key = (int) ke.getKeyChar();
        if (key == 18 && clearButton.isEnabled())       // ctrl + r
            clearAction(null);
        else if (key == 19) {
            saveAction(null);   // ctrl + s
        } 
        else if (key == 15)     // ctrl + o
            openAction(null);
        else if(key == 25 && redoButton.isEnabled())    //ctrl + y
            redoAction(null);
        else if(key == 26 && undoButton.isEnabled())    //ctrl + z
            undoAction(null);
        
        //REMOVE VERTEX OR EDGE
        
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        
    }
    
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) { }
        EventQueue.invokeLater(() -> {
            Automata automata = new Automata();
            automata.setGUI();
            automata.setLayout();
        });
    }
}