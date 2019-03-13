package ysq.fall.convertion;

public class ShortConverter implements Converter {

    public ShortConverter() {
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Short.class)&&!type.equals(short.class))
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return (Short.valueOf(value));
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.ShortConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Short.class)&&!type.equals(short.class))
                || value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        try {
            return (Short.toString(((Number) value).shortValue()));
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
