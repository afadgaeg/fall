package ysq.fall.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TelephoneValidator implements ConstraintValidator<TelephoneValidate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cvc) {
        if (value == null) {
            return true;
        }
        String rex = "^[0-9\\-]{7,20}$";
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(value);
        if (m.find()) {
            return true;
        }
        return false;
    }

    @Override
    public void initialize(TelephoneValidate a) {
    }
}
