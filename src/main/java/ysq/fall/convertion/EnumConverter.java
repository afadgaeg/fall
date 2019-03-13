package ysq.fall.convertion;

import java.lang.reflect.Method;

public class EnumConverter implements Converter {

    public EnumConverter() {
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || !Enum.class.isAssignableFrom(type)
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value);
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.EnumConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) {
        if (type == null || !Enum.class.isAssignableFrom(type)
                || value == null) {
            return (null);
        }
        return value.toString();
    }

}
