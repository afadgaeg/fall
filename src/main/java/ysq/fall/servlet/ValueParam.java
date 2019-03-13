package ysq.fall.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ValueParam extends Param {

    
    private ByteArrayOutputStream buf = new ByteArrayOutputStream();
    private String encoding;

    public ValueParam(String name, String encoding) {
        super(name);
        this.encoding = encoding;
    }

    @Override
    public void appendData(byte[] data, int start, int length) throws IOException {
        buf.write(data, start, length);
    }

    public void complete() throws UnsupportedEncodingException {
        String val = encoding == null ? new String(buf.toByteArray()) : new String(buf.toByteArray(), encoding);
        if (value == null) {
            value = val;
        } else {
            if (!(value instanceof List)) {
                List<String> v = new ArrayList<>();
                v.add((String) value);
                value = v;
            }
            ((List) value).add(val);
        }
        buf.reset();
    }

    
}
