package automata;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FileManager {
    
    private static String printTab(Integer tabStamp) {
        int space = 5;
        String print = "", tab = "";
        for(int i=0; i<space; i++)
            tab += " ";
        for(int i=0; i<tabStamp; i++)
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
    
    public static void saveFile(String filePath, Object... objectList) {
        int stamp = 0;
        final String enter = System.getProperty("line.separator");
        final String commaSeparator = ","+enter;
        final String[] variableName = {"inputAlphabet", "vertexs", "edges"};
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
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
                            bw.write("{"+enter);
                            stamp++;
                            Field[] fields = obj.getClass().getDeclaredFields();
                            for(int k=0; k<fields.length; k++) {
                                Field field = fields[k];
                                field.setAccessible(true);
                                Object ob = field.get(obj);
                                if(ob.getClass().equals(String.class)) 
                                    bw.write(printTab(stamp)+key(field.getName())+"\""+ob+"\"");
                                else if(isPrimitiveType(ob.getClass())) 
                                    bw.write(printTab(stamp)+key(field.getName())+ob);
                                else {
                                    if(field.getType().isArray()) {   //Array of String
                                        bw.write(printTab(stamp)+key(field.getName()));
                                        bw.write("[");
                                        int length = Array.getLength(ob);
                                        if(length == 0) {
                                            bw.write("]"+(k != objectList.length-1 ? commaSeparator : enter));
                                            continue;
                                        }
                                        bw.write(enter);
                                        stamp++;
                                        for(int p=0; p<length; p++) {
                                            bw.write(printTab(stamp)+"\""+Array.get(ob, p)+"\"");
                                            bw.write(p != length-1 ? commaSeparator : enter);
                                        }
                                        bw.write(printTab(--stamp)+"]");
                                    }
                                    else
                                        continue;
                                }
                                bw.write(k != fields.length-1 ? commaSeparator : enter);
                            }
                            bw.write(printTab(--stamp)+"}");
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
            bw.close();
        }
        catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            System.err.println(ex);
        }
    }
        
    public static boolean openFile(String filePath, Object... objectList) {
        ArrayList<Vertex> vertex = new ArrayList<>();
        ArrayList<Edge> edge = new ArrayList<>();
        
        
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
        return false;
    }
}






//        final String enter = "\n";
//        final String commaSeparator = ","+enter;
//        try {
//            System.out.print("{"+enter);
//            for(int i=0; i<objectList.length; i++) {
//                Object object = objectList[i];
//                System.out.print(printTab(++stamp)+key(object.getClass().getName()));
//                if(object.getClass().equals(ArrayList.class)) {
//                    System.out.print("["+enter);
//                    stamp++;
//                    final Method m = object.getClass().getMethod("get", int.class);
//                    Method listSize = object.getClass().getMethod("size");
//                    int size = Integer.parseInt(listSize.invoke(object).toString());
//                    for(int j=0; j<size; j++) {
//                        Object obj = m.invoke(object, j);
//                        System.out.print(printTab(stamp));
//                        if(obj.getClass().equals(String.class))
//                            System.out.print("\""+obj+"\"");
//                        else if(isPrimitiveType(obj.getClass())) 
//                            System.out.print(obj);
//                        else {
//                            System.out.print("{"+enter);
//                            stamp++;
//                            Field[] fields = obj.getClass().getDeclaredFields();
//                            for(int k=0; k<fields.length; k++) {
//                                Field field = fields[k];
//                                field.setAccessible(true);
//                                Object ob = field.get(obj);
//                                if(ob.getClass().equals(String.class)) 
//                                    System.out.print(printTab(stamp)+key(field.getName())+"\""+ob+"\"");
//                                else if(isPrimitiveType(ob.getClass())) 
//                                    System.out.print(printTab(stamp)+key(field.getName())+ob);
//                                else {
//                                    if(field.getType().isArray()) {   //Array of String
//                                        System.out.print(printTab(stamp)+key(field.getName()));
//                                        System.out.print("["+enter);
//                                        stamp++;
//                                        int length = Array.getLength(ob);
//                                        for(int p=0; p<length; p++) {
//                                            System.out.print(printTab(stamp)+"\""+Array.get(ob, p)+"\"");
//                                            System.out.print(p != length-1 ? commaSeparator : enter);
//                                        }
//                                        System.out.print(printTab(--stamp)+"]");
//                                    }
//                                    else
//                                        continue;
//                                }
//                                System.out.print(k != fields.length-1 ? commaSeparator : enter);
//                            }
//                            System.out.print(printTab(--stamp)+"}");
//                        }
//                        System.out.print(j != size-1 ? commaSeparator : enter);
//                    }
//                    System.out.print(printTab(--stamp)+"]"+(i != objectList.length-1 ? commaSeparator : enter));
//                    stamp--;
//                }
//                else if(object.getClass().isArray()) {
//                    System.out.print("["+enter);
//                    stamp++;
//                    int length = Array.getLength(object);
//                    for(int p=0; p<length; p++) {
//                        Object ob = Array.get(object, p);
//                        System.out.print(printTab(stamp));
//                        if(ob.getClass().equals(String.class))
//                            System.out.print("\""+ob+"\"");
//                        else if(isPrimitiveType(ob.getClass())) 
//                            System.out.print(Array.get(ob, p));
//                        System.out.print(p != length-1 ? commaSeparator : enter);
//                    }
//                    System.out.print(printTab(--stamp)+"]");
//                    System.out.print(i != objectList.length-1 ? commaSeparator : enter);
//                    stamp--;
//                }
//            }
//            System.out.print("}");
//        }
//        catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
//            System.err.println(ex);
//        }