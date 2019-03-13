package ysq.fall.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class URL implements Cloneable, Serializable {

    private String value;
    private Map<String, Collection<String>> paramMap = new HashMap<>();

    public URL() {
    }

    public URL(String value) {
        this.value = value;
    }

    public URL(String value, Map<String, Collection<String>> paramMap) {
        this.value = value;
        this.paramMap = paramMap;
    }

    // ---------------------------------------------------------
    public URL putParam(String key, String value) {
        if (paramMap.containsKey(key)) {
            paramMap.get(key).clear();
            paramMap.get(key).add(value);
        } else {
            Collection<String> s = new HashSet<>();
            s.add(value);
            paramMap.put(key, s);
        }
        return this;
    }

    public URL putParam(String key, Collection<String> s) {
        paramMap.put(key, s);
        return this;
    }

    public URL addParam(String key, String value) {
        if (paramMap.containsKey(key)) {
            paramMap.get(key).add(value);
        } else {
            Collection<String> s = new HashSet<>();
            s.add(value);
            paramMap.put(key, s);
        }
        return this;
    }

    public URL removeParam(String key) {
        paramMap.remove(key);
        return this;
    }

    // ---------------------------------------
    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object ob = ois.readObject();
            ois.close();
            return (URL) ob;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString(String encode) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder(value);
        if (paramMap != null && !paramMap.isEmpty()) {
            for (Map.Entry<String, Collection<String>> param : paramMap.entrySet()) {
                String parameterName = param.getKey();
                Collection<String> parameterValues = param.getValue();
                for (String parameterValue : parameterValues) {
                    if (parameterValue != null && !parameterValue.trim().isEmpty()) {
                        builder.append('&');
                        builder.append(parameterName);
                        builder.append('=');
                        builder.append(URLEncoder.encode(parameterValue.trim(), encode));
                    }
                }
            }
            builder.setCharAt(value.length(), '?');
        }
        return builder.toString();
    }

    // -------------------------------------
    public Map<String, Collection<String>> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Collection<String>> paramMap) {
        this.paramMap = paramMap;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
