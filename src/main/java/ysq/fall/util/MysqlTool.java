/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ysq.fall.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author saul
 */
public class MysqlTool {

    public static String getInsertSql(String entityName, String[] fieldNames) {
        StringBuilder values = new StringBuilder();
        StringBuilder names = new StringBuilder();
        for (String fieldName : fieldNames) {//clz.getDeclaredFields()
            names.append(",`").append(JavaTool.camelToUnderline(fieldName)).append("`");
            values.append(",:").append(fieldName);
        }
        return new StringBuilder("insert into `").append(JavaTool.camelToUnderline(entityName)).append("` (")
                .append(names.substring(1)).append(")values(").append(values.substring(1)).append(");").toString();//clz.getSimpleName()
    }

    public static String getUpdateSql(String entityName, String[] fieldNames) {
        StringBuilder updates = new StringBuilder();
        for (String fieldName : fieldNames) {
            if (!"id".equals(fieldName)) {
                updates.append(",`").append(JavaTool.camelToUnderline(fieldName)).append("`=:").append(fieldName);
            }
        }
        return new StringBuilder("update `").append(JavaTool.camelToUnderline(entityName)).append("` set ")
                .append(updates.substring(1)).append(" where `id`=:id;").toString();
    }

    public static String getDeleteSql(String entityName) {
        return new StringBuilder("delete from `").append(JavaTool.camelToUnderline(entityName)).append("` where `id`=:id;").toString();
    }

    public static String getSelectByIdSql(String entityName) {
        return new StringBuilder("select * from `").append(JavaTool.camelToUnderline(entityName)).append("` where `id`=:id;").toString();
    }

    public static String getSelectAllSql(String entityName) {
        return new StringBuilder("select * from `").append(JavaTool.camelToUnderline(entityName)).append("`;").toString();
    }

    public static String getSelectByIdsSql(String entityName, int size) {
        StringBuilder sb = new StringBuilder("select * from `").append(JavaTool.camelToUnderline(entityName)).append("` where `id` in(");
        StringBuilder placeholdersSB = new StringBuilder();
        for (int i = 0; i < size; i++) {
            placeholdersSB.append(",:id").append(i);
        }
        return sb.append(placeholdersSB.substring(1)).append(");").toString();
    }

//    public static String getSelectByFieldsWithoutOrderBysSql(Object entity, String[] fields) throws NoSuchMethodException,
//            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//        Class entityClass = entity.getClass();
//        String entityName = entityClass.getSimpleName();
//        StringBuilder conditionsSB = new StringBuilder();
//        for (String fieldName : fields) {
//            Method getMethod = entityClass.getDeclaredMethod(JavaTool.getGetMethodName(fieldName));
//            Object v = getMethod.invoke(entity, new Object[]{});
//            if (v == null) {
//                continue;
//            }
//            if (v instanceof String && ((String) v).trim().isEmpty()) {
//                continue;
//            }
//            conditionsSB.append(" and `").append(JavaTool.camelToUnderline(fieldName)).append("`=:").append(fieldName).append(" ");
//        }
//        return new StringBuilder("select * from `").append(JavaTool.camelToUnderline(entityName)).append("` ")
//                .append(" where ").append(conditionsSB.toString().trim().substring("and".length())).toString();
//    }

}
