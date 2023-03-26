package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationExcpretion;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        id++;
        film.setId(id);
        films.put(id, film);
        log.debug("Добавен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

        if (films.size() < film.getId() || film.getId() < 1) {
            log.error("Фильма с переданным ID {} не существует", film.getId());
            throw new ValidationExcpretion("Не существует фильма с ID " + film.getId());
        }

        films.put(film.getId(), film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }
}
