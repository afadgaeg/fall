package ysq.fall.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ysq.fall.util.JavaTool;
import ysq.fall.util.Messages;

public class FallServlet extends HttpServlet {

    ServletConfig config;
    public static String MESSAGE = "messages";

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void init(ServletConfig config) {
        this.config = config;
    }

    @Override
    public void destroy() {
        config = null;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String encoding = "utf-8";
        res.setCharacterEncoding(encoding);
        res.setHeader("Content-Type", "text/json");
        res.setHeader("Access-Control-Allow-Origin", "*");
        String clientCharacterEncoding = request.getCharacterEncoding();
        if (clientCharacterEncoding == null || clientCharacterEncoding.equals("")) {
            request.setCharacterEncoding(encoding);
        }
        Messages messages = new Messages();
        req.setAttribute(MESSAGE, messages);
        try {
            String contextPath = req.getContextPath();
            String requestURI = req.getRequestURI();
            String actionStr = requestURI.substring(contextPath.length() + "/api/".length(), requestURI.length() - ".do".length());
            String[] actions = actionStr.split("/");
            String objName = actions[0];
            objName = "com.xxx.action." + JavaTool.firstCharToUpper(objName) + "Action";
            String methodName = JavaTool.underlineToLowerCamel(actions[1]);
            Class objCls = Class.forName(objName);
            Method method = objCls.getMethod(methodName, Class.forName("javax.servlet.http.HttpServletRequest"),
                    Class.forName("javax.servlet.http.HttpServletResponse"));
            Object obj = objCls.newInstance();
            method.invoke(obj, new Object[]{req, res});
        } catch (Throwable ex) {
            ActionTool.outFailedMsg(req, res, JavaTool.getStackTrace(ex));
        }
        messages.clear();
    }

}
