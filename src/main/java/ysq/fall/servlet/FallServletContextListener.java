package ysq.fall.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import ysq.fall.convertion.BigDecimalConverter;
import ysq.fall.convertion.BigIntegerConverter;
import ysq.fall.convertion.BooleanConverter;
import ysq.fall.convertion.ByteConverter;
import ysq.fall.convertion.CharacterConverter;
import ysq.fall.convertion.ConvertController;
import ysq.fall.convertion.DateConverter;
import ysq.fall.convertion.DoubleConverter;
import ysq.fall.convertion.EnumConverter;
import ysq.fall.convertion.FloatConverter;
import ysq.fall.convertion.IntegerConverter;
import ysq.fall.convertion.LongConverter;
import ysq.fall.convertion.ShortConverter;
import ysq.fall.util.RequestParameterUtil;

public class FallServletContextListener implements ServletContextListener {

    public static String REQUEST_PARAMETER_UTIL = "requestParameterUtil";

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        List<String> resourceBundles = new ArrayList<>();
        resourceBundles.add("ysq.fall.messages");

        RequestParameterUtil paramUtil = new RequestParameterUtil();
        paramUtil.setConvertController(getConvertController());
        //paramUtil.setActionParamName("");
        //paramUtil.setIgnoreValidation(true);
        paramUtil.setResourceBundles(resourceBundles);
        sce.getServletContext().setAttribute(REQUEST_PARAMETER_UTIL, paramUtil);

//        Security security = new Security();
//        sce.getServletContext().setAttribute("security", security);
//        security.addIdentityResolver(new IdentityResolverImpl());
//        security.addPermissionResolver(new AllPR());

    }

    private ConvertController getConvertController() {
        ConvertController convertController = new ConvertController();
        convertController.addConverter(new IntegerConverter());
        convertController.addConverter(new BooleanConverter());
        convertController.addConverter(new EnumConverter());
        convertController.addConverter(new FloatConverter());
        convertController.addConverter(new LongConverter());
        convertController.addConverter(new ShortConverter());
        convertController.addConverter(new DoubleConverter());
        convertController.addConverter(new ByteConverter());
        convertController.addConverter(new CharacterConverter());
        convertController.addConverter(new BigIntegerConverter());
        convertController.addConverter(new BigDecimalConverter());
        convertController.addConverter(new DateConverter());
        return convertController;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        RequestParameterUtil paramUtil = (RequestParameterUtil) sce.getServletContext().getAttribute(REQUEST_PARAMETER_UTIL);
        paramUtil.destory();
        sce.getServletContext().removeAttribute(REQUEST_PARAMETER_UTIL);
    }

}
