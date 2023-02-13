package ru.practicum.shareit.booking;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE_USE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DateValidation.class)
public @interface StartBeforeEndDateValid {

    String message() default "Ошибка в датах аренды. Дата start раньше end или равны нулю.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
