package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> getAll() {

        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public Film getById(@PathVariable Integer id) {

        return filmService.findById(id);
    }

    @PostMapping("/films")
    public Film post(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film put(@Valid @RequestBody Film film) {

        return filmService.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film putLike(@PathVariable Integer id, @PathVariable Integer userId) {

        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {

        return filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {

        return filmService.getTopFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {

        return filmService.findAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {

        return filmService.findGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {

        return filmService.findAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {

        return filmService.findMpaById(id);
    }

}
