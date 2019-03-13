package ysq.fall.convertion;

public class BooleanConverter implements Converter {

    public BooleanConverter(){
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Boolean.class) && !type.equals(boolean.class))
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return (Boolean.valueOf(value));
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.BooleanConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Boolean.class) && !type.equals(boolean.class))
                || value == null) {
            return "";
        }

        try {
            return value.toString();
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
