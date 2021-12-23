package automata;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {
    
    private static String printTab(int stamp) {
        int space = 5;
        String print = "", tab = "";
        for(int i=0; i<space; i++)
            tab += " ";
        for(int i=0; i<stamp; i++)
            print += tab;
        return print;
    }
    
    private static String key(String fieldName) {
        return "\""+fieldName+"\": ";
    }
    
    private static boolean isPrimitiveType(Class<?> cls) {
        return  cls.isPrimitive() || 
                cls.equals(Boolean.class) || 
                cls.equals(Integer.class) ||
                cls.equals(Character.class) ||
                cls.equals(Byte.class) ||
                cls.equals(Short.class) ||
                cls.equals(Double.class) ||
                cls.equals(Long.class) ||
                cls.equals(Float.class) 
                ||cls.equals(String.class) 
                ;
    }
    
    /*
    saveFile: receive multiple parameters that must be Array of PrimitiveDataType or ArrayList of (Vertex and Edge) only
    Vertex and Edge Class: accept field { primitive data type (include String) and Array of String ony }, otherwise will skip save that field
    */
    
    public static String objectClass(int stamp, Object obj, int objLength) {
        String enter = System.getProperty("line.separator");
        String commaSeparator = ","+enter;
        String text = "";
        text += ("{"+enter);
        stamp++;
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean isFirst = true;
        for(int k=0; k<fields.length; k++) {
            try {
                Field field = fields[k];
                field.setAccessible(true);
                Object ob = field.get(obj);
                if(ob.getClass().equals(String.class)) {
                    text += ((isFirst ? "" : commaSeparator)+printTab(stamp)+key(field.getName())+"\""+ob+"\"");
                    isFirst = false;
                }
                else if(isPrimitiveType(ob.getClass())) {
                    text += ((isFirst ? "" : commaSeparator)+printTab(stamp)+key(field.getName())+ob);
                    isFirst = false;
                }
                else {
                    if(field.getType().isArray()) {   //Array of String
                        text += ((isFirst ? "" : commaSeparator)+printTab(stamp)+key(field.getName()));
                        text += ("[");
                        isFirst = false;
                        int length = Array.getLength(ob);
                        if(length == 0) {
                            text += ("]"+(k != objLength-1 ? commaSeparator : enter));
                            continue;
                        }
                        text += (enter);
                        stamp++;
                        for(int p=0; p<length; p++) {
                            text += (printTab(stamp)+"\""+Array.get(ob, p)+"\"");
                            text += (p != length-1 ? commaSeparator : enter);
                        }
                        text += (printTab(--stamp)+"]");
                    }
                    else if(field.getType().equals(Vertex.class)) {   //Array of String
                        text += ((isFirst ? "" : commaSeparator)+printTab(stamp)+key(field.getName()));
                        text += ("");
                        text += objectClass(stamp, field.get(obj), 0);
                        text += (enter+printTab(stamp)+"}");
                    }
                }
            } 
            catch (IllegalArgumentException | IllegalAccessException ex) {
                System.err.println(ex);
            }
        }
        return text;
    }
    
    public static void saveFile(String filePath, Object... objectList) {
        int stamp = 0;
        final String enter = System.getProperty("line.separator");
        final String commaSeparator = ","+enter;
        final String[] variableName = {"inputAlphabet", "vertexs", "edges"};
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write("{"+enter);
            stamp++;
            for(int i=0; i<objectList.length; i++) {
                Object object = objectList[i];
                bw.write(printTab(stamp)+key(variableName[i]));
                if(object.getClass().equals(ArrayList.class)) {
                    bw.write("[");
                    final Method m = object.getClass().getMethod("get", int.class);
                    Method listSize = object.getClass().getMethod("size");
                    int size = Integer.parseInt(listSize.invoke(object).toString());
                    if(size == 0) {
                        bw.write("]"+(i != objectList.length-1 ? commaSeparator : enter));
                        continue;
                    }
                    stamp++;
                    bw.write(enter);
                    for(int j=0; j<size; j++) {
                        Object obj = m.invoke(object, j);
                        bw.write(printTab(stamp));
                        if(obj.getClass().equals(String.class))
                            bw.write("\""+obj+"\"");
                        else if(isPrimitiveType(obj.getClass())) 
                            bw.write(obj.toString());
                        else {
                            bw.write(objectClass(stamp, obj, objectList.length));
                            bw.write(enter+printTab(stamp)+"}");
                        }
                        bw.write(j != size-1 ? commaSeparator : enter);
                    }
                    bw.write(printTab(--stamp)+"]"+(i != objectList.length-1 ? commaSeparator : enter));
                }
                else if(object.getClass().isArray()) {
                    bw.write("[");
                    int length = Array.getLength(object);
                    if(length == 0) {
                        bw.write("]"+(i != objectList.length-1 ? commaSeparator : enter));
                        continue;
                    }
                    stamp++;
                    bw.write(enter);
                    for(int p=0; p<length; p++) {
                        Object ob = Array.get(object, p);
                        bw.write(printTab(stamp));
                        if(ob.getClass().equals(String.class))
                            bw.write("\""+ob+"\"");
                        else if(isPrimitiveType(ob.getClass())) 
                            bw.write(Array.get(ob, p).toString());
                        bw.write(p != length-1 ? commaSeparator : enter);
                    }
                    bw.write(printTab(--stamp)+"]");
                    bw.write(i != objectList.length-1 ? commaSeparator : enter);
                }
            }
            bw.write("}");
        }
        catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            System.err.println(ex);
        }
        finally {
            try {
                if(bw != null)
                    bw.close();
            } 
            catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
    
    private static boolean isBracket(String str) {
        boolean bracketOnly = (str.equals("{") || str.equals("}") || str.equals("[") || str.equals("]") || str.equals("},") || str.equals("],"));
        boolean bracketWithKey = (str.contains(": {") || str.contains(": [")) &&
                                 (str.indexOf(": {") == str.lastIndexOf(": {") || str.indexOf(": [") == str.lastIndexOf(": ["));
        return bracketOnly || bracketWithKey;
    }
    
    private static String cutBracket(String str) {
        if(str.contains("{"))
            return "{";
        else if(str.contains("["))
            return "[";
        else if(str.contains("}"))
            return "}";
        return "]";
    }
    
    private static boolean isValidExpression(String expression) {
        if(expression.isEmpty())
            return false;
        Map<Character, Character> openClosePair = new HashMap<>();
        openClosePair.put(')', '(');
        openClosePair.put('}', '{');
        openClosePair.put(']', '[');
        Stack<Character> stack = new Stack<>();
        for (char ch : expression.toCharArray()) {
            if (openClosePair.containsKey(ch)) {
                if (stack.isEmpty() || stack.pop() != openClosePair.get(ch)) {
                    return false;
                }
            } 
            else if (openClosePair.values().contains(ch)) {
                stack.push(ch);
            }
        }
        return stack.isEmpty();
    }
    
    private static int endPoint(int pointer, Object[] objects) {
        String bracketGroup = "";
        for(; pointer<objects.length-1 && !isValidExpression(bracketGroup); pointer++) {
            String object = objects[pointer].toString().trim();
            if(isBracket(object)) {
                bracketGroup += cutBracket(object);
            }
        }
        return pointer-1;
    }
    
    public static boolean openFile(String filePath, Object... objectList) {
        ArrayList<Vertex> vertex = new ArrayList<>();
        ArrayList<Edge> edge = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            Object[] objects = br.lines().toArray();
//            int loadList = 0;
            String[][] groups = new String[objectList.length][];
            int pointer = 0;
            for(int round=0; round<objectList.length; round++) {
                String bracketGroup = "";
                int start = pointer+1;
                while(pointer<objects.length-1 && !isValidExpression(bracketGroup)) {
                    pointer++;
                    String object = objects[pointer].toString().trim();
                    if(isBracket(object)) {
                        bracketGroup += cutBracket(object);
                    }
                }
//                for(; pointer<objects.length-1 && !isValidExpression(bracketGroup); pointer++) {
//                    String object = objects[pointer].toString().trim();
//                    if(isBracket(object)) {
//                        bracketGroup += cutBracket(object);
//                    }
//                }
                int end = pointer;
                String[] subGroup = new String[end-start-1];
                int index = 0;
                for(int i=start+1; i<end; i++) {
                    subGroup[index++] = objects[i].toString().trim();
//                    System.out.println(subGroup[index-1]);
                }
                groups[round] = subGroup;
//                System.out.println(bracketGroup+" : "+start+"-"+end+" : "+(end-start-1));
            }
//            for(String[] objs : groups)
//                for(String obj : objs)
//                    System.out.println(obj);
            for(String[] group : groups) {
                for(String subGroup : group) {
//                    if()
                    System.out.print(subGroup);
                }
                System.out.println("");
            }
        }
        catch(IOException ex) {
            System.err.println(ex);
        } 
//        catch (IllegalArgumentException | IllegalAccessException ex) {
//            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
        finally {
            try {
                if(br != null)
                    br.close();
            } 
            catch (IOException ex) {
                System.err.println(ex);
            }
        }
        
        
        // check inputAlphabetSet must match with each edges and not be empty
        
        
        // if data correct, then copy vertex and edge to vertexs and edges
        // otherwise, then not change (include must not vertex name repeat)
        
        
        
        // Absolute correct format and consistency of data, then copy vertex and edge to array of formal parameter
        // include Array of String (String[] inputAlphabet)
        
        
        
        // Linked vertex object reference to each edges
//        for(Vertex v : vertexs) 
//            v.setSelected(false);
//        for (Edge e : edges) {
//            e.setSelected(false);
//            if (e.getInputState() != null) {
//                String name = e.getInputState().getName();
//                for (Vertex v : vertexs) {
//                    if (v.getName().equals(name)) {
//                        e.setInputState(v);
//                        break;
//                    }
//                }
//            }
//            if (e.getOutputState() != null) {
//                String name = e.getOutputState().getName();
//                for (Vertex v : vertexs) {
//                    if (v.getName().equals(name)) {
//                        e.setOutputState(v);
//                        break;
//                    }
//                }
//            }
//        }
        return true;
    }
}