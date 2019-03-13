package ysq.fall.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MultipartFilter implements Filter {

    public static final String MULTIPART = "multipart/";

    private boolean createTempFiles = false;

    private int maxRequestSize = 0;

    public boolean getCreateTempFiles() {
        return createTempFiles;
    }

    public void setCreateTempFiles(boolean createTempFiles) {
        this.createTempFiles = createTempFiles;
    }

    public int getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(int maxFileSize) {
        this.maxRequestSize = maxFileSize;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (isMultipartRequest(httpRequest)) {
            MultipartRequest multipartRequest = new MultipartRequestImpl(httpRequest, createTempFiles,
                    maxRequestSize);
            // Force the request to be parsed now
            multipartRequest.getParameterNames();
            chain.doFilter(multipartRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }

        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }

        if (contentType.toLowerCase().startsWith(MULTIPART)) {
            return true;
        }

        return false;
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
