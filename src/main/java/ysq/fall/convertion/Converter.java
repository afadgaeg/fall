package ysq.fall.convertion;

public interface Converter {

    public Object getAsObject (String value, Class type) throws ConvertException;

    public String getAsString(Object value, Class type) throws ConvertException;

}
