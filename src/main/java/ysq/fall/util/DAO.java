package ysq.fall.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;

public class DAO {

    public static int update(Connection conn, Object entity, String[] fieldNames) throws SQLException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        return executeUpdate(conn, MysqlTool.getUpdateSql(entity.getClass().getSimpleName(), fieldNames), entity);
    }
    
    public static int insert(Connection conn, Object entity, String[] fieldNames) throws SQLException, IllegalAccessException, 
            InvocationTargetException, NoSuchMethodException {
        return executeUpdate(conn, MysqlTool.getInsertSql(entity.getClass().getSimpleName(), fieldNames), entity);
    }

    public static <T> T getBeanById(Connection conn, String id, Class<T> entityClass) throws SQLException, IllegalAccessException,
            NoSuchMethodException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        String selectSql = MysqlTool.getSelectByIdSql(entityClass.getSimpleName());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        T entity = DAO.getBean(conn, selectSql, paramMap, entityClass);
        return entity;
    }

    public static <T> List<T> getBeanListByIds(Connection conn, Collection ids, Class<T> entityClass) throws SQLException, IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchMethodException, NoSuchFieldException {
        List<T> entitys;
        Map<String, Object> paramMap = new HashMap<>();
        int i = 0;
        for (Object id : ids) {
            paramMap.put(new StringBuilder("id").append(i).toString(), id);
            i++;
        }
        entitys = DAO.getBeanList(conn, MysqlTool.getSelectByIdsSql(entityClass.getSimpleName(), ids.size()), paramMap, entityClass);
        return entitys;
    }

    //--------------------------------------
    public static int add(Connection conn, String sql, Object bean)
            throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql, Statement.RETURN_GENERATED_KEYS);
        warper.bind(bean);
        try (PreparedStatement stmt = warper.getStmt();) {
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public static int executeUpdate(Connection conn, String sql, Map<String, Object> paramMap)
            throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(paramMap);
        try (PreparedStatement stmt = warper.getStmt();) {
            return stmt.executeUpdate();
        }
    }

    public static int executeUpdate(Connection conn, String sql, Object bean)
            throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(bean);
        try (PreparedStatement stmt = warper.getStmt();) {
            return stmt.executeUpdate();
        }
    }

    public static Long getScalar(Connection conn, String sql, Map<String, Object> paramMap)
            throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(paramMap);
        try (PreparedStatement stmt = warper.getStmt();
                ResultSet rs = stmt.executeQuery();) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return null;
    }

    public static Long getScalar(Connection conn, String sql, Object bean)
            throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(bean);
        try (PreparedStatement stmt = warper.getStmt();
                ResultSet rs = stmt.executeQuery();) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return null;
    }

    public static <T> T getBean(Connection conn, String sql, Map<String, Object> paramMap, Class<T> type)
            throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            NoSuchFieldException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(paramMap);
        try (PreparedStatement stmt = warper.getStmt();
                ResultSet rs = stmt.executeQuery();) {
            return getBean(rs, type);
        }
    }

    public static <T> T getBean(Connection conn, String sql, Object bean, Class<T> type)
            throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            NoSuchFieldException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(bean);
        try (PreparedStatement stmt = warper.getStmt();
                ResultSet rs = stmt.executeQuery();) {
            return getBean(rs, type);
        }
    }

    public static <T> List<T> getBeanList(Connection conn, String sql, Map<String, Object> paramMap, Class<T> type)
            throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException,
            NoSuchFieldException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(paramMap);
        try (PreparedStatement stmt = warper.getStmt();
                ResultSet rs = stmt.executeQuery();) {
            return getBeanList(rs, type);
        }
    }

    public static <T> List<T> getBeanList(Connection conn, String sql, Object bean, Class<T> type)
            throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException,
            NoSuchFieldException {
        PreparedStatementWrapper warper = new PreparedStatementWrapper(conn, sql);
        warper.bind(bean);
        try (PreparedStatement stmt = warper.getStmt();
                ResultSet rs = stmt.executeQuery();) {
            return getBeanList(rs, type);
        }
    }

    // --------------------------------------------
    static private <T> T getCurrentBean(ResultSet rs, Class<T> type)
            throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException,
            NoSuchFieldException, NoSuchMethodException {
        T bean = type.newInstance();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            String name = rsmd.getColumnLabel(i);
            Object value = rs.getObject(name);
            if (value != null) {
                String fieldName = JavaTool.underlineToLowerCamel(name);
                Field field = type.getDeclaredField(fieldName);
                Class fieldType = field.getType();
                if (fieldType.isEnum()) {
                    Method valuesMethod = fieldType.getMethod("values");
                    Enum[] values = (Enum[]) valuesMethod.invoke(null);
                    value = values[(Integer) value];
                }
                BeanUtils.setProperty(bean, fieldName, value);
            }
        }
        return bean;
    }

    static public <T> T getBean(ResultSet rs, Class<T> type)
            throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException,
            NoSuchFieldException, NoSuchMethodException {
        if (rs.next()) {
            return getCurrentBean(rs, type);
        }
        return null;
    }

    static public <T> List<T> getBeanList(ResultSet rs, Class<T> type)
            throws SQLException, IllegalAccessException, InvocationTargetException, InstantiationException,
            NoSuchFieldException, NoSuchMethodException {
        List<T> beans = new ArrayList<>();
        while (rs.next()) {
            T bean = getCurrentBean(rs, type);
            beans.add(bean);
        }
        return beans;
    }

}
