package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable String id) {

        try {
            return filmStorage.findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("Переданый ID: {} не является целым числом", id);
            throw new IdPassingException(String.format("Переданый ID: %s не является целым числом",
                    id));
        }
    }

    @PostMapping
    public Film post(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {

        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable String id, @PathVariable String userId) {

        try {
            return filmService.addLike(Integer.parseInt(id), Integer.parseInt(userId));
        } catch (NumberFormatException e) {
            log.error("Один или оба переданных ID: {}, {} не являются целым числом", id, userId);
            throw new IdPassingException(String.format("Один или оба переданных ID: %s," +
                    " %s не являются целым числом", id, userId));
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable String id, @PathVariable String userId) {
        try {
            return filmService.removeLike(Integer.parseInt(id), Integer.parseInt(userId));
        } catch (NumberFormatException e) {
            log.error("Один или оба переданных ID: {}, {} не являются целым числом", id, userId);
            throw new IdPassingException(String.format("Один или оба переданных ID: %s," +
                    " %s не являются целым числом", id, userId));
        }
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") String count) {

        try {
            return filmService.getTopFilms(Integer.parseInt(count));
        } catch (NumberFormatException e) {
            log.error("Переданый ID: {} не является целым числом", count);
            throw new IdPassingException(String.format("Переданый ID: %s не является целым числом",
                    count));
        }
    }
}
