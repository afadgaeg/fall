package ysq.fall.convertion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter {

    DateFormat formator = new SimpleDateFormat("yyyy-MM-dd");

    public DateConverter() {
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || !type.equals(Date.class) || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return formator.parse(value);
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.DateConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || !type.equals(Date.class)
                || value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        try {
            return formator.format(((Date) value));
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
