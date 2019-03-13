package ysq.fall.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class URLUtil implements Serializable {

    public static URL createURL(HttpServletRequest req, String str) {
        StringBuilder builder = new StringBuilder();
        builder.append(req.getScheme()).append("://");
        builder.append(req.getLocalName());
        Integer port = req.getLocalPort();
        if (port != null && port != 80) {
            builder.append(":").append(port);
        }
        builder.append(req.getContextPath()).append(str);
        return new URL(builder.toString());
    }

    public static URL createURL(HttpServletRequest req) {

        StringBuilder builder = new StringBuilder();
//        builder.append(req.getScheme()).append("://");
//        builder.append(req.getLocalName());
//        Integer port = req.getLocalPort();
//        if (port != null && port != 80) {
//            builder.append(":").append(port);
//        }
//        builder.append(req.getContextPath());
//        builder.append(req.getServletPath());
        builder.append(req.getRequestURL());
        Map<String, Collection<String>> paramMap = new HashMap<>();
        if (req.getMethod().equalsIgnoreCase("get")) {
            Map<String, String[]> pMap = req.getParameterMap();
            for (Entry<String, String[]> entry : pMap.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                Collection<String> o = new HashSet<>();
                o.addAll(Arrays.asList(value));
                paramMap.put(key, o);
            }
        }
        return new URL(builder.toString(), paramMap);
    }

    public static String encodeRedirectURL(URL url, HttpServletResponse res)
            throws UnsupportedEncodingException {
        return res.encodeRedirectURL(encode(url, res.getCharacterEncoding()));
    }

    public static String encodeURL(URL url, HttpServletResponse res)
            throws UnsupportedEncodingException {
        return res.encodeURL(encode(url, res.getCharacterEncoding()));
    }

    public static String encode(URL url, String characterEncoding)
            throws UnsupportedEncodingException {
        String value = url.getValue();
        StringBuilder builder = new StringBuilder(value);
        Map<String, Collection<String>> parameters = url.getParamMap();
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, Collection<String>> param : parameters.entrySet()) {
                String parameterName = param.getKey();
                Collection<String> parameterValues = param.getValue();
                for (String parameterValue : parameterValues) {
                    builder.append('&');
                    builder.append(parameterName);
                    builder.append('=');
                    builder.append(encode(parameterValue, characterEncoding));
                }
            }
            builder.setCharAt(value.length(), '?');
        }
        return builder.toString();
    }

    public static String encode(String str, String characterEncoding) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, characterEncoding);
    }

    public static String decode(String str, String characterEncoding) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, characterEncoding);
    }
}
