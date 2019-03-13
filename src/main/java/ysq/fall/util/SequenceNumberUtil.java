package ysq.fall.util;

import java.io.Serializable;

public class SequenceNumberUtil implements Serializable {

    long i;

    public SequenceNumberUtil() {
        i = 0;
    }

    public String getSequenceString() {
        i++;
        String str;
        if (i <= 999999) {
            str = String.format("000000", i);
        } else {
            str = String.valueOf(i);
        }
        return str;
    }

    public String getSequenceFileName() {
        synchronized (SequenceNumberUtil.class) {
            return String.valueOf(System.currentTimeMillis()) + getSequenceString();
        }
    }
}
