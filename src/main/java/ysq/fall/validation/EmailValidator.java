package ysq.fall.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValidate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cvc) {
        if (value == null) {
            return true;
        }
        int i = value.length();
        if (i > 50 || i < 5) {
            return false;
        }
        String rex = "^[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+$";
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(value);
        if (m.find()) {
            return true;
        }
        return false;
    }

    @Override
    public void initialize(EmailValidate a) {
    }
}
