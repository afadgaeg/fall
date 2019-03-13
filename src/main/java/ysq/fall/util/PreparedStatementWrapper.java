package ysq.fall.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.beanutils.PropertyUtils;

public class PreparedStatementWrapper {

    private PreparedStatement stmt;
    Map<String, Integer> paramMap;

    public PreparedStatementWrapper(Connection conn, String namedParamSql) throws SQLException {
        this(conn, namedParamSql, null);
    }

    public PreparedStatementWrapper(Connection conn, String namedParamSql, Integer statement) throws SQLException {
        paramMap = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(":\\w+");
        Matcher m = p.matcher(namedParamSql);
        int i = 1;
        while (m.find()) {
            String paramName = m.group().substring(1);
            paramMap.put(paramName, i);
            m.appendReplacement(sb, "?");
            i++;
        }
        m.appendTail(sb);
        if (statement == null) {
            stmt = conn.prepareStatement(sb.toString());
        } else {
            stmt = conn.prepareStatement(sb.toString(), statement);
        }
    }

    public void bind(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
        if (bean != null) {
            for (String paramName : paramMap.keySet()) {
                Object paramValue = PropertyUtils.getProperty(bean, paramName);
                if(paramValue instanceof Enum ){
                    paramValue = ((Enum)paramValue).ordinal();
                }
                stmt.setObject(paramMap.get(paramName), paramValue);
            }
        }
    }

    public void bind(Map<String, Object> namedParamMap)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
        if (namedParamMap != null) {
            for (String paramName : paramMap.keySet()) {
                Object paramValue = namedParamMap.get(paramName);
                if(paramValue instanceof Enum ){
                    paramValue = ((Enum)paramValue).ordinal();
                }
                stmt.setObject(paramMap.get(paramName), paramValue);
            }
        }
    }

    public PreparedStatement getStmt() {
        return stmt;
    }

}
