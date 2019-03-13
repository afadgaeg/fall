package ysq.fall.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class StringUtil {

    public static String randomString(int length) {
        StringBuilder buffer = new StringBuilder("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    public static String unsplit(String[] strs, String split) {
        StringBuilder b = new StringBuilder(strs[0]);
        for (int i = 1; i < strs.length; i++) {
            b.append(split).append(strs[i]);
        }
        return b.toString();
    }

    public static String unsplit(Set<String> strs, String split) {
        StringBuilder b = new StringBuilder();
        for (String str : strs) {
            b.append(split).append(str);
        }
        return b.toString().substring(1);
    }

    public static String unsplit(List<String> strs, String split) {
        StringBuilder b = new StringBuilder(strs.get(0));
        for (int i = 1; i < strs.size(); i++) {
            b.append(split).append(strs.get(i));
        }
        return b.toString();
    }
//    public static String replaceFirst(String str, String str1, String str2){
//        return Pattern.compile(str1, Pattern.LITERAL).matcher(str).replaceFirst(str2);
//    }
//
//    public static String replace(String str, String str1, String str2){
//        return Pattern.compile(str1, Pattern.LITERAL).matcher(str).replaceAll(str2);
//    }
}
