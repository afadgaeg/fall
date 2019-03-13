package ysq.fall.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Param {

    protected Object value = null;
    private String name;

    public Param(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public abstract void appendData(byte[] data, int start, int length) throws IOException;
}
