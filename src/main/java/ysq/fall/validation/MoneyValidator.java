package ysq.fall.validation;

import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MoneyValidator implements ConstraintValidator<MoneyValidate, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext cvc) {
        if (value == null) {
            return true;
        }
        if (value.scale()!=2) {
            return false;
        }
        return true;
    }

    @Override
    public void initialize(MoneyValidate a) {
    }
}
