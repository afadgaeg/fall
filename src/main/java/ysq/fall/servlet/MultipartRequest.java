package ysq.fall.servlet;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface MultipartRequest extends HttpServletRequest {

    public Param getParam(String name);

    public List<Param> getParams(String name);
}
