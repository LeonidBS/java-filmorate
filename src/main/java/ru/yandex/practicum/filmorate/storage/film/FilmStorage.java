package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public interface FilmStorage {
    List<Film> findAll();

    Film findById(Integer id);

    Film create(Film film);

    Film update(Film film);

    List<Film> topFilms(Integer count);

    Film addLike(Integer filmId, Integer userId);

    Film removeLike(Integer filmId, Integer userId);

    List<Genre> findAllGenres();

    Genre findGenreById(Integer id);

    List<Mpa> findAllMpa();

    Mpa findMpaById(Integer id);
}
