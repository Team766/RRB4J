package com.team766.rrb4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Scanner;

public class RRB4J_Test {
    private static Object toObject(Type clazz, String value) {
        if (Boolean.TYPE == clazz) {
            if ("true".equalsIgnoreCase(value)) return true;
            if ("false".equalsIgnoreCase(value)) return false;
            return null;
        }
        if (Byte.TYPE == clazz) return Byte.parseByte(value);
        if (Short.TYPE == clazz) return Short.parseShort(value);
        if (Integer.TYPE == clazz) return Integer.parseInt(value);
        if (Long.TYPE == clazz) return Long.parseLong(value);
        if (Float.TYPE == clazz) return Float.parseFloat(value);
        if (Double.TYPE == clazz) return Double.parseDouble(value);
        if (String.class == clazz) return value;
        System.err.println("Unsupported argument type");
        return null;
    }
    
    public static void main(String[] args) {
        RRB4J rr = new RRB4J();
        
        Scanner in = new Scanner(System.in);
        while(true) {
            System.err.print("> ");
            String command = in.nextLine();
            if ("".equals(command)) continue;
            String[] paramStrs = command.split("\\s+");
            try {
                boolean found = false;
                for (Method m : RRB4J.class.getDeclaredMethods()) {
                    String mname = m.getName();
                    if (!mname.equals(paramStrs[0])) continue;
                    Type[] pType = m.getGenericParameterTypes();
                    if (pType.length != paramStrs.length - 1) continue;
                    found = true;
                    Object[] params = new Object[paramStrs.length - 1];
                    for (int i = 0; i < params.length; ++i) {
                        try {
                            params[i] = toObject(pType[i], paramStrs[i+1]);
                        } catch (NumberFormatException e) {
                            found = false;
                            break;
                        }
                        if (params[i] == null) {
                            found = false;
                            break;
                        }
                    }
                    if (!found) continue;
                    try {
                        Object o = m.invoke(rr, params);
                        if (o != null) {
                            System.out.println(o);
                        }
                    } catch (InvocationTargetException x) {
                        Throwable cause = x.getCause();
                        System.err.format("invocation of %s failed: %s%n", mname, cause.getMessage());
                    }
                    break;
                }
                if (!found) {
                  System.err.println("No method found for " + command);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }
}
