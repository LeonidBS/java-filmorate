package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    private LocalDate startDate;

    @Override
    public void initialize(AfterDate afterDateAnnotation) {
        startDate = LocalDate.parse(afterDateAnnotation.date());
    }

    @Override
    public boolean isValid(LocalDate filmReleaseDate,
                           ConstraintValidatorContext cxt) {
        if (filmReleaseDate != null) {
            return filmReleaseDate.isAfter(startDate);
        }
        return false;
    }
}