/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ysq.fall.servlet;

import ysq.fall.FallException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author Administrator
 */
public class CharacterEncodingHttpServletRequest extends HttpServletRequestWrapper {
    private final String encoding;
    private final String originalEncoding;
    private final Map<String, String> paramMap = new HashMap<>();
    private final Map<String, String[]> paramsMap = new HashMap<>();
    

    public CharacterEncodingHttpServletRequest(HttpServletRequest request, String encoding, String originalEncoding) {
        super(request);
        this.encoding = encoding;
        if (originalEncoding != null && !originalEncoding.equals("")) {
            this.originalEncoding = originalEncoding;
        } else {
            this.originalEncoding = "ISO-8859-1";
        }
    }

    @Override
    public String getParameter(String key) {
        String value = paramMap.get(key);
        if (value == null) {
            value = super.getParameter(key);
            if (value != null) {
                value = decode(value);
                paramMap.put(key, value);
            }
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String key) {
        String[] values = paramsMap.get(key);
        if (values == null) {
            values = super.getParameterValues(key);
            if (values != null && values.length != 0) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = decode(values[i]);
                }
            }
        }
        return values;
    }

    protected String decode(String str) {
        try {
            if (str != null && !str.equals("")) {
                str = new String(str.getBytes(originalEncoding), encoding);
            }
            return str;
        } catch (UnsupportedEncodingException ex) {
            throw new FallException(ex);
        }
    }
    
}
