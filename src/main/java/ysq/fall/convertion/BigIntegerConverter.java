package ysq.fall.convertion;

import java.math.BigInteger;

public class BigIntegerConverter implements Converter {

    public BigIntegerConverter(){
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || !type.equals(BigInteger.class)
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }

        try {
            return new BigInteger(value);
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.BigIntegerConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {

        if (type == null || !type.equals(BigInteger.class)
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
