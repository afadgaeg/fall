package ysq.fall.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

public class CharacterEncodingFilter implements Filter {

    private String encoding;
    private String originalEncoding = null;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (encoding != null) {
            response.setCharacterEncoding(encoding);
        }
        String clientCharacterEncoding = request.getCharacterEncoding();
        if (encoding != null && (clientCharacterEncoding == null || clientCharacterEncoding.equals(""))) {
            request.setCharacterEncoding(encoding);
        }
        if (encoding != null && httpRequest.getMethod().equalsIgnoreCase("get")) {
            filterChain.doFilter(new CharacterEncodingHttpServletRequest(httpRequest, encoding, originalEncoding), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getOriginalEncoding() {
        return originalEncoding;
    }

    public void setOriginalEncoding(String originalEncoding) {
        this.originalEncoding = originalEncoding;
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        encoding = fc.getInitParameter("encoding");
        originalEncoding = fc.getInitParameter("originalEncoding");
    }

    @Override
    public void destroy() {
    }

}
