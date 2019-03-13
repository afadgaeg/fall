package ysq.fall.convertion;

public class IntegerConverter implements Converter {

    public IntegerConverter() {
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Integer.class)&&!type.equals(int.class))
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return (Integer.valueOf(value));
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.IntegerConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Integer.class)&&!type.equals(int.class))
                || value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        try {
            return (Integer.toString(((Number) value).intValue()));
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
