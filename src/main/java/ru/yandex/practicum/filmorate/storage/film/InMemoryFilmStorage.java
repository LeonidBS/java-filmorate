package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.UpdateFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public List<Film> findAll() {

        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Integer id) {
        Film film = films.get(id);

        if (film == null) {
            log.error("Фильм с переданным ID {} не существует", id);
            throw new IdNotFoundException("Не существует фильма с ID: " + id);
        }

        return film;
    }

    @Override
    public Film create(Film film) {
        id++;
        film.setId(id);
        films.put(id, film);
        log.debug("Добавен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {

        if (!films.containsKey((film.getId()))) {
            log.error("Ошибка идентификации фильма. Не существует фильма с ID {}", film.getId());
            throw new UpdateFilmException("Ошибка идентификации фильма. Не существует фильма с ID " + film.getId());
        }

        films.put(film.getId(), film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }
}
