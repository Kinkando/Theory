package automata;

import java.util.Stack;
import java.util.ArrayList;

public class StateHandle {
    private final Stack<ArrayList<Object>> listUndo, listRedo, undo, redo;
    
    public StateHandle() {
        listUndo = new Stack<>();
        listRedo = new Stack<>();
        undo = new Stack<>();
        redo = new Stack<>();
    }
    
    public void undo(ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        final ArrayList<Object> inputRedo = new ArrayList<>();
        final ArrayList<Object> inputListRedo = new ArrayList<>();
        for(Vertex v : vertexs) {
            inputListRedo.add(insertVertex(v));
            inputRedo.add(v);
        }
        for(Edge ed : edges) {
            inputListRedo.add(insertEdge(ed));
            inputRedo.add(ed);
        }
        // Remove same top of stack with current state
        while(!isChange(vertexs, edges, inputListRedo, inputRedo)) {
            if(!undo.isEmpty() && !listUndo.isEmpty()) {
                undo.pop();
                listUndo.pop();
            }
        }
        putBackHandle(undo, listUndo, vertexs, edges);
        redo.push(!inputRedo.isEmpty() ? inputRedo : new ArrayList<>());
        listRedo.push(!inputListRedo.isEmpty() ? inputListRedo : new ArrayList<>());
    }
    
    public void redo(ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        putBackHandle(redo, listRedo, vertexs, edges);
        undo.push(!redo.isEmpty() ? redo.pop() : new ArrayList<>());
        listUndo.push(!listRedo.isEmpty() ? listRedo.pop() : new ArrayList<>());
    }
    
    private void putBackHandle(Stack<ArrayList<Object>> real, Stack<ArrayList<Object>> list, ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        vertexs.clear();
        edges.clear();
        if(!real.isEmpty() && !list.isEmpty()) {
            real.peek().forEach((obj) -> {
                if(obj instanceof Vertex) 
                    vertexs.add((Vertex)obj);
                else 
                    edges.add((Edge)obj);
            });
            int countV=0,countE=0;
            for(Object obj : list.peek()) {
                if(obj instanceof Vertex) 
                    setVertex(vertexs.get(countV++),(Vertex)obj);
                else
                    setEdge(edges.get(countE++),(Edge)obj);
            }
        }
    }
    
    private boolean compareEqual(Object obj1, Object obj2) {
        if(obj1 instanceof Vertex && obj2 instanceof Vertex) {
            Vertex v1 = (Vertex) obj1;
            Vertex v2 = (Vertex) obj2;
            return  v1.getName().equals(v2.getName()) && v1.getX()==v2.getX() && v1.getY()==v2.getY() && 
                    v1.isInitialState()==v2.isInitialState() && v1.isAcceptedState()==v2.isAcceptedState();
        }
        else if(obj1 instanceof Edge && obj2 instanceof Edge) {
            Edge e1 = (Edge) obj1;
            Edge e2 = (Edge) obj2;
            return  e1.getString().equals(e2.getString()) && 
                    e1.getXCenter()==e2.getXCenter() && e1.getYCenter()==e2.getYCenter() && 
                    e1.getXCharacter()==e2.getXCharacter() && e1.getYCharacter()==e2.getYCharacter();
        }
        return false;
    }
    
    private Vertex insertVertex(Vertex v) {
        Vertex vertex = new Vertex(v.getX(), v.getY());
        vertex.setName(v.getName());
        vertex.setInitialState(v.isInitialState());
        vertex.setAcceptedState(v.isAcceptedState());
        return vertex;
    }
    
    private Edge insertEdge(Edge ed) {
        Edge edge = new Edge(ed.getInputState(), ed.getOutputState(), ed.getCharacter());
        edge.setXCenter(ed.getXCenter());
        edge.setYCenter(ed.getYCenter());
        edge.setXCharacter(ed.getXCharacter());
        edge.setYCharacter(ed.getYCharacter());
        return edge;
    }
    
    private void setVertex(Vertex vertex, Vertex v) {
        vertex.setX(v.getX());
        vertex.setY(v.getY());
        vertex.setName(v.getName());
        vertex.setInitialState(v.isInitialState());
        vertex.setAcceptedState(v.isAcceptedState());
        vertex.setSelected(false);
    }
    
    private void setEdge(Edge edge, Edge ed) {
        edge.setCharacter(ed.getCharacter());
        edge.setXCenter(ed.getXCenter());
        edge.setYCenter(ed.getYCenter());
        edge.setXCharacter(ed.getXCharacter());
        edge.setYCharacter(ed.getYCharacter());
        edge.setSelected(false);
    }
    
    public boolean isChange(ArrayList<Vertex> vertexs, ArrayList<Edge> edges, ArrayList<Object> action, ArrayList<Object> listAction) {
        ArrayList<Object> undoItem = !undo.isEmpty() ? undo.peek() : new ArrayList<>();
        ArrayList<Object> listUndoItem = !listUndo.isEmpty() ? listUndo.peek() : new ArrayList<>();
        if(undoItem.size()==action.size() || listUndoItem.size()==listAction.size()) {
            boolean change = false;
            for(int i=0; i<undoItem.size() && !change; i++) {
                if(undoItem.get(i) instanceof Vertex && listUndoItem.get(i) instanceof Vertex &&
                   action.get(i) instanceof Vertex && listAction.get(i) instanceof Vertex) {
                    Vertex v1 = (Vertex) listUndoItem.get(i);
                    Vertex v2 = (Vertex) listAction.get(i);
                    change = !compareEqual(v1, v2);
                }
                else if(undoItem.get(i) instanceof Edge && listUndoItem.get(i) instanceof Edge &&
                        action.get(i) instanceof Edge && listAction.get(i) instanceof Edge) {
                    Edge e1 = (Edge) listUndoItem.get(i);
                    Edge e2 = (Edge) listAction.get(i);
                    change = !compareEqual(e1, e2);
                }
                else
                    change = true;
            }
            return change;
        }
        return true;
    }
    
    public void changeHandle(ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        ArrayList<Object> action = new ArrayList<>();
        ArrayList<Object> listAction = new ArrayList<>();
        vertexs.forEach((v) -> {
            action.add(v);
            listAction.add(insertVertex(v));
        });
        edges.forEach((e) -> {
            action.add(e);
            listAction.add(insertEdge(e));
        });
        if(isChange(vertexs, edges, action, listAction)) {
            undo.add(action);
            listUndo.add(listAction);
            redo.clear();
            listRedo.clear();
        }
    }
    
    public boolean undoIsEnabled() {
        return !undo.isEmpty();
    }
    
    public boolean redoIsEnabled() {
        return !redo.isEmpty();
    }
}
