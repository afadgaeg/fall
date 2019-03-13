package ysq.fall.util;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathCodeUtil implements Serializable {

    private String split = "\\.";
    private String unsplit;
    private int length;

    // ----------------------------

    public PathCodeUtil(int length, String split, String unsplit) {
        this.split = split;
        this.unsplit = unsplit;
        this.length = length;
    }

    public PathCodeUtil() {
        this(1, "\\.", ".");
    }

    public PathCodeUtil(int length) {
        this(length, "\\.", ".");
    }

    public PathCodeUtil(String split) {
        this(1, split, split);
    }

    public PathCodeUtil(int length, String split) {
        this(length, split, split);
    }

    // ---------------------------------------

    public String fill(long point) {
        String str = String.valueOf(point);
        while (str.length() < length) {
            str = "0" + str;
        }
        return str;
    }

    public long nextPoint(long point) {
        if (point < 0);
        String str = String.valueOf(point);
        long i;
        double max = Math.pow(10, length);
        long result;
        if (str.length() <= length) {
            i = Long.parseLong(str);
            if (i == max-1) {
                result = (long) (Long.parseLong(str) * max);
            } else {
                result = i + 1;
            }
        } else {
            String tail = str.substring(str.length() - length);
            i = Long.parseLong(tail);
            if (i == max-1) {
                result =  (long) (Long.parseLong(str) * max);
            } else {
                result = (long) (Long.parseLong(str.substring(0, str.length() - length)) * max + i + 1);
            }
        }
        return result;
    }

    public String sub(String up, long point) {
        return up + unsplit + fill(point);
    }

    public String right(String str) {
        String[] strs = str.split(split);
        String result;
        if (strs.length < 2) {
            result = String.valueOf(nextPoint(Long.parseLong(strs[0])));
        } else {
            strs[strs.length - 1] = fill(nextPoint(Long.parseLong(strs[strs.length - 1])));
            result = StringUtil.unsplit(strs, split);
        }
        return result;
    }

    public static String updatePathCode(String path, String from, String to) {
        Pattern pattern = Pattern.compile(from);
        Matcher matcher = pattern.matcher(path);
        return matcher.replaceFirst(to);
    }
}
