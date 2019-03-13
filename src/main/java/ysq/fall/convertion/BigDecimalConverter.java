package ysq.fall.convertion;

import java.math.BigDecimal;

public class BigDecimalConverter implements Converter {

    public BigDecimalConverter(){
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || !type.equals(BigDecimal.class)
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.BigDecimalConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || !type.equals(BigDecimal.class)
                || value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }
        try {
            return (value.toString());
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
