package automata;

import java.util.Stack;
import java.util.ArrayList;

public class StateHandle {
    private final Stack<ArrayList<Object>> undo, redo;
    
    public StateHandle() {
        undo = new Stack<>();
        redo = new Stack<>();
    }
    
    public void undo(ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        final ArrayList<Object> inputRedo = new ArrayList<>();
        vertexs.forEach((v) -> {
            inputRedo.add(new Vertex(v));
        });
        edges.forEach((ed) -> {
            inputRedo.add(new Edge(ed));
        });
        // Remove same top of stack with current state
        while(!isChange(vertexs, edges, inputRedo)) {
            if(!undo.isEmpty()) {
                undo.pop();
            }
        }
        backtrack(undo, vertexs, edges);
        redo.push(!inputRedo.isEmpty() ? inputRedo : new ArrayList<>());
    }
    
    public void redo(ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        backtrack(redo, vertexs, edges);
        undo.push(!redo.isEmpty() ? redo.pop() : new ArrayList<>());
    }
    
    private void backtrack(Stack<ArrayList<Object>> stack, ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        vertexs.clear();
        edges.clear();
        if(!stack.isEmpty()) {
            stack.peek().forEach((obj) -> {
                if(obj instanceof Vertex) 
                    vertexs.add(new Vertex((Vertex)obj));
                else 
                    edges.add(new Edge((Edge)obj));
            });
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
    
    public boolean isChange(ArrayList<Vertex> vertexs, ArrayList<Edge> edges, ArrayList<Object> action) {
        ArrayList<Object> undoItem = !undo.isEmpty() ? undo.peek() : new ArrayList<>();
        if(undoItem.size()==action.size()) {
            boolean change = false;
            for(int i=0; i<undoItem.size() && !change; i++) {
                change = !compareEqual(undoItem.get(i), action.get(i));
            }
            return change;
        }
        return true;
    }
    
    public void changeHandle(ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        ArrayList<Object> action = new ArrayList<>();
        vertexs.forEach((v) -> {
            action.add(new Vertex(v));
        });
        edges.forEach((e) -> {
            action.add(new Edge(e));
        });
        if(isChange(vertexs, edges, action)) {
            undo.add(action);
            redo.clear();
        }
    }
    
    public boolean undoIsEnabled() {
        return !undo.isEmpty();
    }
    
    public boolean redoIsEnabled() {
        return !redo.isEmpty();
    }
}
