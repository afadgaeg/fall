package ysq.fall.convertion;

public class CharacterConverter implements Converter {

    public CharacterConverter() {
    }

    @Override
    public Object getAsObject(String value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Character.class) && !type.equals(char.class))
                || value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() < 1) {
            return null;
        }
        try {
            return (value.charAt(0));
        } catch (Exception ex) {
            String msg = "com.hdgang.fall.message.exception.ConvertException.CharacterConverter.getAsObject";
            throw new ConvertException(msg, ex);
        }
    }

    @Override
    public String getAsString(Object value, Class type) throws ConvertException {
        if (type == null || (!type.equals(Character.class) && !type.equals(char.class))
                || value == null) {
            return "";
        }

        try {
            return (value.toString());
        } catch (Exception e) {
            throw new ConvertException("", e);
        }

    }

}
