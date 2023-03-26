package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class DurationValidator implements ConstraintValidator<DurationIsPositive, Duration> {

    @Override
    public void initialize(DurationIsPositive durationIsPositive) {
    }

    @Override
    public boolean isValid(Duration filmDuration, ConstraintValidatorContext cxt) {
        return !filmDuration.isNegative() && !filmDuration.isZero();
    }
}