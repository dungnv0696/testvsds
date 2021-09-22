package com.lifesup.gbtd.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {FieldValueValidator.class})
public @interface FieldValue {

    String message() default "No value found";

    int[] numbers() default {};

    String[] strings() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}