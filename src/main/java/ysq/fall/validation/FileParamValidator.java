package ysq.fall.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import ysq.fall.servlet.FileParam;

public class FileParamValidator implements ConstraintValidator<FileParamValidate, FileParam> {

    private int maxSize;
    private String[] types;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public boolean isValid(FileParam value, ConstraintValidatorContext cvc) {
        if (value == null) {
            return true;
        }
        if (value.getFileSize() > getMaxSize()) {
            return false;
        }
        if (types != null && types.length > 0) {
            for (String type : types) {
                if (type.equalsIgnoreCase(value.getContentType())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void initialize(FileParamValidate parameters) {
        setTypes(parameters.types());
        setMaxSize(parameters.maxSize());
    }
}
