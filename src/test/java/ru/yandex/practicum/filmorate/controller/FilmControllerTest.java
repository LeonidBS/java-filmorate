package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationExcpretion;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void dateValidatorFilmWhenReleaseDateIsBeforeReleaseOfFirstFilm() {
        Film film = new Film(1, "name", "description",
                LocalDate.parse("1895-12-27"), Duration.parse("PT90M"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void durationValidatorFilmWhenDescriptionLengthMoreThat200() {
        StringBuilder descriptionString = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            descriptionString.append("a");
        }
        Film film = new Film(1, "name", descriptionString.toString(),
                LocalDate.parse("2000-01-01"), Duration.parse("PT90M"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void updateFilmWhenIdIsNotExist() {
        Film film1 = new Film("name", "description",
                LocalDate.parse("1995-12-27"), Duration.parse("PT90M"));
        Film film2 = new Film(2, "Updated name", "Updated description",
                LocalDate.parse("1995-12-27"), Duration.parse("PT95M"));
        FilmController filmController = new FilmController();
        filmController.create(film1);

        Executable executable = () -> filmController.update(film2);

        ValidationExcpretion validationExcpretion = assertThrows(ValidationExcpretion.class, executable);
        assertEquals("Не существует фильма с ID " + film2.getId(),
                validationExcpretion.getMessage());
    }

}