package ysq.fall.convertion;

public class LongConverter implements Converter {

    public LongConverter() {
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Long.class)&&!type.equals(long.class))
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return (Long.valueOf(value));
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.LongConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Long.class)&&!type.equals(long.class))
                || value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        try {
            return (Long.toString(((Number) value).longValue()));
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
