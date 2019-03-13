package ysq.fall.convertion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个链加converterMap的转换请求值的转换控制器.
 * 当一个String 类型的参数到来并被要求转换成某个type时,查找converterMap中是否有以type为键的转换器.如果有,由该转换器负责转换请求参数
 * 如果没有,则执行处理链,
 * 当链中某个转换器返回非空值时,将该转换器和type作为一对key/value保存到converterMap中,返回转换后的值
 */
public class ConvertController implements Serializable {

    private final Map<Class, Converter> converterMap = new ConcurrentHashMap<>();
    private final List<Converter> converters = new ArrayList<>();

    /**
     *
     */
    public void destory() {
        converterMap.clear();
        converters.clear();
    }

    public ConvertController() {
        
    }

    public void addConverter(Converter converter) {
        converters.add(converter);
    }

    public Object convert(String value, Class type) throws ConvertException {
        if (value == null || type == null || type.equals(String.class)) {
            return value;
        }
        if(converterMap.containsKey(type)){
            Converter converter = converterMap.get(type);
            return converter.getAsObject(value, type);
        }else{
            for (Converter converter : converters) {
                Object convertedValue = converter.getAsObject(value, type);
                if (convertedValue != null) {
                    converterMap.put(type, converter);
                    return convertedValue;
                }
            }
        }
        return null;
    }

}
