package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static Validator validator;

    private Mpa mpa = new Mpa(1, "G");

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void dateValidatorFilmWhenReleaseDateIsBeforeReleaseOfFirstFilm() {
        Film film = new Film(1, "name", "description",
                LocalDate.parse("1895-12-27"), 90, new HashMap<>(),
                mpa, new ArrayList<>());

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
                LocalDate.parse("2000-01-01"), 90, new HashMap<>(),
                mpa, new ArrayList<>());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void getTopFilmsFilmWhenCountIsNotCorrect() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        FilmService filmService = new FilmService(inMemoryFilmStorage, userService);

        for (int i = 1; i < 16; i++) {
            inMemoryFilmStorage.create(new Film("name" + i, "description" + i,
                    LocalDate.parse("1995-12-27").minusYears(i), 90 + i, new HashMap<>(),
                    mpa, new ArrayList<>()));
        }

        for (int i = 1; i < 5; i++) {
            inMemoryUserStorage.create(new User("email@leo" + i + ".ru", "login" + i,
                    "name" + i, LocalDate.parse("1995-12-27").plusMonths(i)));
        }

        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 3; j++) {
                filmService.addLike(i + 3, j);
            }
        }

        Executable executable = () -> filmService.getTopFilms(0);

        ValidationException validationException = assertThrows(ValidationException.class, executable);
        assertEquals("Запрошено не корректное количество фильмов: " + 0,
                validationException.getMessage());
    }
}