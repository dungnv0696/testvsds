package com.lifesup.gbtd.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;

public class FieldValueValidator implements ConstraintValidator<FieldValue, Object> {

    protected FieldValue constraintAnnotation;

    @Override
    public void initialize(FieldValue constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = validateField(value);
        if (!result) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    constraintAnnotation.message()
            )
                    .addConstraintViolation();
        }
        return result;
    }

    private boolean validateField(Object value) {
        if (Objects.isNull(value)) {
            return true;
        }
        if (value instanceof Integer) {
            Integer typedField = (Integer) value;
            return Arrays.stream(constraintAnnotation.numbers())
                    .anyMatch(v -> Objects.equals(v, typedField));
        }
        if (value instanceof String) {
            String typedField = (String) value;
            return Arrays.stream(constraintAnnotation.strings())
                    .anyMatch(v -> v.equalsIgnoreCase(typedField));
        }
        return false;
    }
}
