package ysq.fall.convertion;

public class ByteConverter implements Converter {

    public ByteConverter(){
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Byte.class) && !type.equals(byte.class))
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return Byte.valueOf(value);
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.ByteConverter.getAsObject";
            throw new ConvertException(msg, ex);
        } 
    }
    
    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Byte.class) && !type.equals(byte.class))
                || value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        try {
            return (Byte.toString(((Byte) value).byteValue()));
        } catch (Exception e) {
            throw new ConvertException("", e);
        }
    }

}
