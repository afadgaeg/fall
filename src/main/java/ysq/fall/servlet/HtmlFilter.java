package ysq.fall.servlet;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HtmlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        chain.doFilter(new HtmlHttpServletRequest(httpRequest), response);
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {

    }

    @Override
    public void destroy() {
        
    }

    public class HtmlHttpServletRequest extends HttpServletRequestWrapper {

        public HtmlHttpServletRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String key){
            String result = super.getParameter(key);
            if(result!=null) result = result.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
            return result;
        }

        @Override
        public String[] getParameterValues(String key){
            String[] results = super.getParameterValues(key);
            if(results!=null)
                for(int i=0;i<results.length;i++)
                    results[i] = results[i].replaceAll(">", "&gt;").replaceAll("<", "&lt;");
            return results;
        }

    }

}
