package ysq.fall.convertion;

public class DoubleConverter implements Converter {

    public DoubleConverter() {
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || !type.equals(Double.class)
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return (Double.valueOf(value));
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.DoubleConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || !type.equals(Double.class)
                || value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        try {
            return (Double.toString(((Number) value).doubleValue()));
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
