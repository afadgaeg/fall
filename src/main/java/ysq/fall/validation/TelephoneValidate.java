package ysq.fall.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = TelephoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TelephoneValidate {

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String message() default "{com.hdgang.fall.message.validation.telephone}";
}
