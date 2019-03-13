package ysq.fall;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.MessageInterpolator;

public class FallMessageInterpolator implements MessageInterpolator {

    private static final Pattern messageParameterPattern = Pattern.compile("(\\{[^\\}]+?\\})");
    String resourceBundle;

    public FallMessageInterpolator(String resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Override
    public String interpolate(String message, Context context) {
        return interpolateMessage(message, context.getConstraintDescriptor().getAttributes(), Locale.getDefault());
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        return interpolateMessage(message, context.getConstraintDescriptor().getAttributes(), locale);
    }

    private String interpolateMessage(String message, Map<String, Object> annotationParameters, Locale locale) {
        ResourceBundle userResourceBundle = ResourceBundle.getBundle(resourceBundle, locale);
        message = replaceVariables(message, userResourceBundle, locale, true);
        message = replaceAnnotationAttributes(message, annotationParameters);
        message = message.replace("\\{", "{");
        message = message.replace("\\}", "}");
        message = message.replace("\\\\", "\\");
        return message;
    }

    public String replaceVariables(String message, ResourceBundle bundle, Locale locale, boolean recurse) {
        Matcher matcher = messageParameterPattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        String resolvedParameterValue;
        while (matcher.find()) {
            String parameter = matcher.group(1);
            resolvedParameterValue = resolveParameter(parameter, bundle, locale, recurse);
            matcher.appendReplacement(sb, escapeMetaCharacters(resolvedParameterValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String replaceAnnotationAttributes(String message, Map<String, Object> annotationParameters) {
        Matcher matcher = messageParameterPattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String resolvedParameterValue;
            String parameter = matcher.group(1);
            Object variable = annotationParameters.get(removeCurlyBrace(parameter));
            if (variable != null) {
                resolvedParameterValue = escapeMetaCharacters(variable.toString());
            } else {
                resolvedParameterValue = parameter;
            }
            matcher.appendReplacement(sb, resolvedParameterValue);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String resolveParameter(String parameterName, ResourceBundle bundle, Locale locale, boolean recurse) {
        String parameterValue;
        try {
            if (bundle != null) {
                parameterValue = bundle.getString(removeCurlyBrace(parameterName));
                if (recurse) {
                    parameterValue = replaceVariables(parameterValue, bundle, locale, recurse);
                }
            } else {
                parameterValue = parameterName;
            }
        } catch (MissingResourceException e) {
            parameterValue = parameterName;
        }
        return parameterValue;
    }

    private String removeCurlyBrace(String parameter) {
        return parameter.substring(1, parameter.length() - 1);
    }

    private String escapeMetaCharacters(String s) {
        String escapedString = s.replace("\\", "\\\\");
        escapedString = escapedString.replace("$", "\\$");
        return escapedString;
    }
}
