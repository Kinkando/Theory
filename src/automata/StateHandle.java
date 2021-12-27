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
        ArrayList<Vertex> vertex = new ArrayList<>();
        ArrayList<Edge> edge = new ArrayList<>();
        vertexs.forEach((v) -> {
            vertex.add(Vertex.factory(v));
        });
        edges.forEach((e) -> {
            edge.add(Edge.factory(e));
        });
        connect(vertex, edge);
        ArrayList<Object> current = addList(vertex, edge);
        // Remove same top of stack with current state
        while(!isChange(current)) {
            if(!undo.isEmpty()) {
                undo.pop();
            }
        }
        backtrack(undo, vertexs, edges);
        redo.push(!current.isEmpty() ? current : new ArrayList<>());
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
                    vertexs.add(Vertex.factory((Vertex)obj));
                else 
                    edges.add(Edge.factory((Edge)obj));
            });
            connect(vertexs, edges);
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
    
    private boolean isChange(ArrayList<Object> action) {
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
        ArrayList<Vertex> vertex = new ArrayList<>();
        ArrayList<Edge> edge = new ArrayList<>();
        vertexs.forEach((v) -> {
            vertex.add(Vertex.factory(v));
        });
        edges.forEach((e) -> {
            edge.add(Edge.factory(e));
        });
        connect(vertex, edge);
        ArrayList<Object> action = addList(vertex, edge);
        if(isChange(action)) {
            undo.add(action);
            redo.clear();
        }
    }
    
    private ArrayList<Object> addList(ArrayList<Vertex> vertex, ArrayList<Edge> edge) {
        ArrayList<Object> list = new ArrayList<>();
        vertex.forEach((v) -> {
            list.add(v);
        });
        edge.forEach((e) -> {
            list.add(e);
        });
        return list;
    }
    
    private void connect(ArrayList<Vertex> vertexs, ArrayList<Edge> edges) {
        // Add reference object of Vertex to Edge (if Vertex change, then Edge change, too)
        for (Edge e : edges) {
            e.setSelected(false);
            if (e.getInputState() != null) {
                String name = e.getInputState().getName();
                for (Vertex v : vertexs) {
                    if (v.getName().equals(name)) {
                        e.setInputState(v);
                        break;
                    }
                }
            }
            if (e.getOutputState() != null) {
                String name = e.getOutputState().getName();
                for (Vertex v : vertexs) {
                    if (v.getName().equals(name)) {
                        e.setOutputState(v);
                        break;
                    }
                }
            }
        }
    }
    
    public boolean undoIsEnabled() {
        return !undo.isEmpty();
    }
    
    public boolean redoIsEnabled() {
        return !redo.isEmpty();
    }
}
