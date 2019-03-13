package ysq.fall.util;

import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import ysq.fall.Constants;
import ysq.fall.FallException;
import ysq.fall.FallMessageInterpolator;
import ysq.fall.convertion.ConvertController;
import ysq.fall.convertion.ConvertException;
import ysq.fall.servlet.FileParam;
import ysq.fall.servlet.MultipartRequest;
import ysq.fall.servlet.Param;

public class RequestParameterUtil implements Serializable {

    private boolean ignoreValidation = false;
    private ConvertController convertController;
    private final Map<Locale, Validator> localeValidatorMap;
    private List<String> resourceBundles;

    public RequestParameterUtil() {
        localeValidatorMap = new ConcurrentHashMap<>();
    }

    public void destory() {
        convertController.destory();
        localeValidatorMap.clear();
        resourceBundles.clear();
    }

    public boolean bind(HttpServletRequest req, Object bean, String[] fieldNames, Messages messages)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException {
        boolean ok = true;
        for (String fieldName : fieldNames) {
            boolean itemOk = true;
            try {
                Class beanCls = bean.getClass();
                Field field = beanCls.getDeclaredField(fieldName);
                Class fieldCls = field.getType();
                String setMethodName = JavaTool.getSetMethodName(fieldName);
                Method method = beanCls.getDeclaredMethod(setMethodName, fieldCls);
                if (Collection.class.isAssignableFrom(fieldCls)) {
                    ParameterizedType filedPT = (ParameterizedType) field.getGenericType();
                    Class fieldCollectionElementCls = (Class) filedPT.getActualTypeArguments()[0];
                    if (FileParam.class.isAssignableFrom(fieldCollectionElementCls)) {
                        MultipartRequest mreq = getMultipartRequest(req);
                        if (mreq != null) {
                            List<Param> params = mreq.getParams(fieldName);
                            if (params != null) {
                                Collection collection = (Collection) fieldCls.newInstance();
                                for (Param param : params) {
                                    if (param instanceof FileParam) {
                                        collection.add((FileParam) param);
                                    }
                                }
                                method.invoke(bean, new Object[]{collection});
                            }
                        }
                    } else {
                        String[] paramValues = req.getParameterValues(fieldName);
                        if (paramValues != null) {
                            Collection collection = (Collection) fieldCls.newInstance();
                            for (String paramValue : paramValues) {
                                collection.add(convert(paramValue, fieldCollectionElementCls));
                            }
                            method.invoke(bean, new Object[]{collection});
                        }
                    }
                } else {
                    if (FileParam.class.isAssignableFrom(fieldCls)) {
                        MultipartRequest mreq = getMultipartRequest(req);
                        if (mreq != null) {
                            Param param = mreq.getParam(fieldName);
                            if (param != null) {
                                if (param instanceof FileParam) {
                                    FileParam fp = (FileParam) param;
                                    if (fp.getFileSize() != 0) {
                                        method.invoke(bean, new Object[]{fp});
                                    }
                                }
                            }
                        }
                    } else {
                        String paramValue = req.getParameter(fieldName);
                        method.invoke(bean, new Object[]{convert(paramValue, fieldCls)});
                    }
                }
            } catch (ConvertException ce) {
                ok = false;
                itemOk = false;
                String msg = getMessage(ce.getMessage(), req.getLocale());
                messages.addToControl(fieldName, Message.Severity.ERROR, msg, msg);
            }
            if (itemOk && !ignoreValidation) {
                if (!validate(bean, fieldName, getValidator(req.getLocale()), messages)) {
                    ok = false;
                }
            }
        }
        return ok;
    }

    public boolean bind(HttpServletRequest req, Object bean, JSONObject jsonObj, String[] fieldNames,
            Messages messages) throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        return bind(req, bean, jsonObj, fieldNames, messages, "");
    }

    public boolean bind(HttpServletRequest req, Object bean, JSONObject jsonObj, String[] fieldNames,
            Messages messages, String prefix) throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        boolean ok = true;
        for (String fieldName : fieldNames) {
            boolean itemOk = true;
            Class beanCls = bean.getClass();
            Field field = beanCls.getDeclaredField(fieldName);
            Class fieldCls = field.getType();
            Method method = beanCls.getDeclaredMethod(JavaTool.getSetMethodName(fieldName), fieldCls);
            try {
                Object value = jsonObj.getObject(fieldName, fieldCls);
                method.invoke(bean, new Object[]{value});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ok = false;
                itemOk = false;
                String msg = ex.getMessage();
                messages.addToControl(prefix + fieldName, Message.Severity.ERROR, msg, msg);
            }
            if (itemOk && !ignoreValidation) {
                if (!validate(bean, fieldName, getValidator(req.getLocale()), messages, prefix)) {
                    ok = false;
                }
            }
        }
        return ok;
    }

//    public boolean bind(HttpServletRequest req,Object bean,Map<String,Object> paramMap,
//            String[] paramNames, Messages messages, String prefix) {
//        boolean b = true;
//        for (String paramName : paramNames) {
//            Object value = paramMap.get(paramName);
//            try {
//                BeanUtils.setProperty(bean, paramName, value);////
//            } catch (IllegalAccessException|InvocationTargetException  ex) {
//                b = false;
//                String msg = ex.getMessage();
//                messages.addToControl(prefix+paramName, Message.Severity.ERROR, msg, msg);
//                continue;
//            }
//            Validator validator = getValidator(req.getLocale());
//            Set<ConstraintViolation<Object>> constraintViolations = validator.validateProperty(bean, paramName);
//            if (!constraintViolations.isEmpty()) {
//                b = false;
//                for (ConstraintViolation constraintViolation : constraintViolations) {
//                    String msg = constraintViolation.getMessage();
//                    messages.addToControl(prefix + paramName, Message.Severity.ERROR, msg, msg);
//                }
//            }
//        }
//        return b;
//    }
    

    private Object convert(String value, Class clazz) throws ConvertException {
        return clazz.equals(String.class) ? value : convertController.convert(value, clazz);
    }

    private boolean validate(Object bean, String paramName, Validator validator, Messages messages) {
        return validate(bean, paramName, validator, messages, "");
    }

    private boolean validate(Object bean, String paramName, Validator validator, Messages messages, String prefix) {
        Set<ConstraintViolation<Object>> constraintViolations;
        constraintViolations = validator.validateProperty(bean, paramName);
        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation constraintViolation : constraintViolations) {
                String msg = constraintViolation.getMessage();
                messages.addToControl(prefix + paramName, Message.Severity.ERROR, msg, msg);
            }
            return false;
        }
        return true;
    }

    private MultipartRequest getMultipartRequest(HttpServletRequest req) {
        if (req instanceof HttpServletRequestWrapper) {
            ServletRequest reqTemp = req;
            while (true) {
                if (reqTemp == null) {
                    break;
                } else if (reqTemp instanceof MultipartRequest) {
                    return (MultipartRequest) reqTemp;
                } else {
                    if (!(reqTemp instanceof HttpServletRequestWrapper)) {
                        break;
                    }
                    reqTemp = ((HttpServletRequestWrapper) reqTemp).getRequest();
                }
            }
        }
        return null;
    }

    public Validator getValidator(final Locale locale) {
        Validator validator;
        if (localeValidatorMap.containsKey(locale)) {
            validator = localeValidatorMap.get(locale);
        } else {
            final Configuration<?> configure = Validation.byDefaultProvider().configure();
            configure.messageInterpolator(new MessageInterpolator() {

                private List<MessageInterpolator> messageInterpolators;

                private List<MessageInterpolator> getMessageInterpolators() {
                    if (messageInterpolators == null) {
                        messageInterpolators = new ArrayList<>();
//                        messageInterpolators.add(new FallMessageInterpolator(Constants.FALL_DEFAULT_RESOURCE_BUNDLE));
                        if (resourceBundles != null) {
                            for (String resourceBundle : resourceBundles) {
                                messageInterpolators.add(new FallMessageInterpolator(resourceBundle));
                            }
                        }
                        messageInterpolators.add(configure.getDefaultMessageInterpolator());
                    }
                    return messageInterpolators;
                }

                @Override
                public String interpolate(String key, Context context) {
                    return interpolate(key, context, locale);
                }

                @Override
                public String interpolate(String key, Context context, Locale locale) {
                    for (MessageInterpolator m : getMessageInterpolators()) {
                        try {
                            String msg = m.interpolate(key, context, locale);
                            if (msg != null && !msg.equals(key)) {
                                return msg;
                            }
                        } catch (MissingResourceException ex) {
                        }
                    }
                    throw new FallException("can not find validator message " + key);
                }
            });
            ValidatorFactory vf = configure.buildValidatorFactory();
            validator = vf.getValidator();
            localeValidatorMap.put(locale, validator);
        }
        return validator;
    }

    private String getMessage(String key, Locale locale, Object... arguments) {
        String msg = null;
        if (resourceBundles != null) {
            for (int i = resourceBundles.size() - 1; i >= 0; i--) {
                String rb = resourceBundles.get(i);
                try {
                    msg = ResourceBundle.getBundle(rb, locale).getString(key);
                } catch (MissingResourceException ex) {
                }
                if (msg != null) {
                    break;
                }
            }
        }
        if (msg == null) {
            try {
                msg = ResourceBundle.getBundle(Constants.FALL_DEFAULT_RESOURCE_BUNDLE, locale).getString(key);
            } catch (MissingResourceException ex) {
            }
        }
        if (msg != null && arguments != null) {
            msg = MessageFormat.format(msg, arguments);
        }
        return msg;
    }

    public void setConvertController(ConvertController convertController) {
        this.convertController = convertController;
    }

    public void setResourceBundles(List<String> resourceBundles) {
        this.resourceBundles = resourceBundles;
    }

    public boolean isIgnoreValidation() {
        return ignoreValidation;
    }

    public void setIgnoreValidation(boolean ignoreValidation) {
        this.ignoreValidation = ignoreValidation;
    }

}
