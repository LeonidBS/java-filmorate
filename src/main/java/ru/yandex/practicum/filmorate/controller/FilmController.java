package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationExcpretion;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/film")
public class FilmController {
    private final List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> findAll() {
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(films.size());
        films.add(film);
        log.debug("Добавен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        if (films.size() <= film.getId()) {
            log.error("Фильма с переданным ID {} не существует", film.getId());
            throw new ValidationExcpretion("Не существует фильма с ID " + film.getId());
        }

        films.set(film.getId(), film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }
}
