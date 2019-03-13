package ysq.fall.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author saul
 */
public class JavaTool {

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    public static String underlineToUpperCamel(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        String[] ws = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : ws) {
            sb.append(firstCharToUpper(w));
        }
        return sb.toString();
    }

    public static String underlineToLowerCamel(String s) {
        return firstCharToLower(underlineToUpperCamel(s));
    }

    public static String firstCharToUpper(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    public static String firstCharToLower(String name) {
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return name;
    }

    public static String camelToUnderline(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (i == 0 || Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.substring(1);
    }

    public static String getSetMethodName(String fieldName) {
        return "set" + firstCharToUpper(fieldName);
    }

    public static String getGetMethodName(String fieldName) {
        return "get" + firstCharToUpper(fieldName);
    }

    public static Class getFieldClass(Class beanCls, String fieldName) throws NoSuchFieldException {
        Field field = beanCls.getDeclaredField(fieldName);
        Class fieldCls = field.getType();
        return fieldCls;
    }

    public static Method getSetMethod(Class beanCls, String fieldName) throws NoSuchFieldException, NoSuchMethodException {
        Class fieldCls = getFieldClass(beanCls, fieldName);
        return beanCls.getDeclaredMethod(getSetMethodName(fieldName), fieldCls);
    }

}
