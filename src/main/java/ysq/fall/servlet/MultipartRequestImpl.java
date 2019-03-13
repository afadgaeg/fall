package ysq.fall.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MultipartRequestImpl extends HttpServletRequestWrapper implements MultipartRequest {

    private static final String PARAM_NAME = "name";
    private static final String PARAM_FILENAME = "filename";
    private static final String PARAM_CONTENT_TYPE = "Content-Type";
    private static final int BUFFER_SIZE = 2048;
    private static final int CHUNK_SIZE = 512;
    private boolean createTempFiles;
    private String encoding = null;
    private Map<String, Param> parameters = null;
    private HttpServletRequest request;
    private static final Pattern PARAM_VALUE_PATTERN = Pattern.compile("^\\s*([^\\s=]+)\\s*[=:]\\s*(.+)\\s*$");

    private enum ReadState {
        BOUNDARY, HEADERS, DATA
    }
    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final byte[] CR_LF = {CR, LF};

    public MultipartRequestImpl(HttpServletRequest request, boolean createTempFiles,
            int maxRequestSize) {
        super(request);
        this.request = request;
        this.createTempFiles = createTempFiles;

        String contentLength = request.getHeader("Content-Length");
        if (contentLength != null && maxRequestSize > 0
                && Integer.parseInt(contentLength) > maxRequestSize) {
            throw new FileUploadException("Multipart request is larger than allowed size");
        }
    }

    private void parseRequest() {

        byte[] boundaryMarker = getBoundaryMarker(request.getContentType());
        if (boundaryMarker == null) {
            throw new FileUploadException("The request was rejected because "
                    + "no multipart boundary was found");
        }

        encoding = request.getCharacterEncoding();
        parameters = new HashMap<>();

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            Map<String, String> headers = new HashMap<>();

            ReadState readState = ReadState.BOUNDARY;

            InputStream input = request.getInputStream();
            int read = input.read(buffer);
            int pos = 0;

            Param p = null;

            while (read != -1) {
                for (int i = 0; i < read; i++) {
                    switch (readState) {
                        case BOUNDARY: {
                            if (checkSequence(buffer, i, boundaryMarker) && checkSequence(buffer, i + 2, CR_LF)) {
                                readState = ReadState.HEADERS;
                                i += 2;
                                pos = i + 1;
                            }
                            break;
                        }
                        case HEADERS: {
                            if (checkSequence(buffer, i, CR_LF)) {
                                String param = (encoding == null)
                                        ? new String(buffer, pos, i - pos - 1)
                                        : new String(buffer, pos, i - pos - 1, encoding);
                                parseParams(param, ";", headers);

                                if (checkSequence(buffer, i + CR_LF.length, CR_LF)) {
                                    readState = ReadState.DATA;
                                    i += CR_LF.length;
                                    pos = i + 1;

                                    String paramName = headers.get(PARAM_NAME);
                                    if (paramName != null) {
                                        if (headers.containsKey(PARAM_FILENAME)) {
                                            FileParam fp = new FileParam(paramName);
                                            if (createTempFiles) {
                                                fp.createTempFile();
                                            }
                                            fp.setContentType(headers.get(PARAM_CONTENT_TYPE));
                                            fp.setFilename(headers.get(PARAM_FILENAME));
                                            if (parameters.containsKey(paramName)) {
                                                p = parameters.get(paramName);
                                                ((FileParam)p).add(fp);
                                                p = fp;
                                            } else {
                                                p = fp;
                                                parameters.put(paramName, fp);  
                                                ((FileParam)p).add(fp);
                                            }
                                        } else {
                                            if (parameters.containsKey(paramName)) {
                                                p = parameters.get(paramName);
                                            } else {
                                                p = new ValueParam(paramName, encoding);
                                                parameters.put(paramName, p);
                                            }
                                        }
                                    }

                                    headers.clear();
                                } else {
                                    pos = i + 1;
                                }
                            }
                            break;
                        }
                        case DATA: {
                            // If we've encountered another boundary...
                            if (checkSequence(buffer, i - boundaryMarker.length - CR_LF.length, CR_LF)
                                    && checkSequence(buffer, i, boundaryMarker)) {
                                // Write any data before the boundary (that hasn't already been written) to the param
                                if (pos < i - boundaryMarker.length - CR_LF.length - 1) {
                                    p.appendData(buffer, pos, i - pos - boundaryMarker.length - CR_LF.length - 1);
                                }

                                if (p instanceof ValueParam) {
                                    ((ValueParam) p).complete();
                                }

                                if (checkSequence(buffer, i + CR_LF.length, CR_LF)) {
                                    i += CR_LF.length;
                                    pos = i + 1;
                                } else {
                                    pos = i;
                                }

                                readState = ReadState.HEADERS;
                            } // Otherwise write whatever data we have to the param
                            else if (i > (pos + boundaryMarker.length + CHUNK_SIZE + CR_LF.length)) {
                                p.appendData(buffer, pos, CHUNK_SIZE);
                                pos += CHUNK_SIZE;
                            }
                            break;
                        }
                    }
                }

                if (pos < read) {
                    // move the bytes that weren't read to the start of the buffer
                    int bytesNotRead = read - pos;
                    System.arraycopy(buffer, pos, buffer, 0, bytesNotRead);
                    read = input.read(buffer, bytesNotRead, buffer.length - bytesNotRead);
                    read += bytesNotRead;
                } else {
                    read = input.read(buffer);
                }

                pos = 0;
            }
        } catch (IOException ex) {
            throw new FileUploadException("IO Error parsing multipart request", ex);
        }
    }

    private byte[] getBoundaryMarker(String contentType) {
        Map<String, Object> params = parseParams(contentType, ";");
        String boundaryStr = (String) params.get("boundary");

        if (boundaryStr == null) {
            return null;
        }

        try {
            return boundaryStr.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return boundaryStr.getBytes();
        }
    }
    
    private boolean checkSequence(byte[] data, int pos, byte[] seq) {
        if (pos - seq.length < -1 || pos >= data.length) {
            return false;
        }

        for (int i = 0; i < seq.length; i++) {
            if (data[(pos - seq.length) + i + 1] != seq[i]) {
                return false;
            }
        }

        return true;
    }

    private Map parseParams(String paramStr, String separator) {
        Map<String, String> paramMap = new HashMap<>();
        parseParams(paramStr, separator, paramMap);
        return paramMap;
    }

    private void parseParams(String paramStr, String separator, Map paramMap) {
        String[] parts = paramStr.split("[" + separator + "]");

        for (String part : parts) {
            Matcher m = PARAM_VALUE_PATTERN.matcher(part);
            if (m.matches()) {
                String key = m.group(1);
                String value = m.group(2);

                // Strip double quotes
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                paramMap.put(key, value);
            }
        }
    }

    // ----------------------------------------

    @Override
    public Param getParam(String name) {
        if (parameters == null) {
            parseRequest();
        }
        return parameters.get(name);
    }
    
    /**
     *
     * @param name
     * @return
     */
    @Override
    public List<Param> getParams(String name){
        if (parameters == null) {
            parseRequest();
        }
        return (List<Param>) parameters.get(name).getValue();
    }

    // ----------------------------------------------

    /**
     *
     * @return
     */
    @Override
    public Enumeration getParameterNames() {
        if (parameters == null) {
            parseRequest();
        }

        return Collections.enumeration(parameters.keySet());
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public String getParameter(String name) {
        Param p = getParam(name);
        if (p != null && p instanceof ValueParam) {
            ValueParam vp = (ValueParam) p;
            if (vp.getValue() instanceof String) {
                return (String) vp.getValue();
            }
        } else if (p != null && p instanceof FileParam) {
            return "---BINARY DATA---";
        } else {
            return super.getParameter(name);
        }

        return null;
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public String[] getParameterValues(String name) {
        Param p = getParam(name);
        if (p != null && p instanceof ValueParam) {
            ValueParam vp = (ValueParam) p;
            if (vp.getValue() instanceof List) {
                List vals = (List) vp.getValue();
                String[] values = new String[vals.size()];
                vals.toArray(values);
                return values;
            } else {
                return new String[]{(String) vp.getValue()};
            }
        } else {
            return super.getParameterValues(name);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Map getParameterMap() {
        if (parameters == null) {
            parseRequest();
        }

        Map<String, Object> params = new HashMap<String, Object>(super.getParameterMap());

        for (String name : parameters.keySet()) {
            Param p = parameters.get(name);
            if (p instanceof ValueParam) {
                ValueParam vp = (ValueParam) p;
                if (vp.getValue() instanceof String) {
                    params.put(name, vp.getValue());
                } else if (vp.getValue() instanceof List) {
                    params.put(name, getParameterValues(name));
                }
            }
        }

        return params;
    }
}
