package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Emoji;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("db") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }


    public List<Film> findAll() {

        return filmStorage.findAll();
    }

    public Film findById(Integer id) {
        Film film = filmStorage.findById(id);

        if (film == null) {
            log.error("Фильм с переданным ID {} не существует", id);
            throw new IdNotFoundException("Не существует фильма с ID: " + id);
        }

        return film;
    }

    public Film create(Film film) {
        log.debug("Добавен фильм: {}", film);

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        findById(film.getId());

        if (filmStorage.findById(film.getId()) == null) {
            log.error("Ошибка идентификации фильма. Не существует фильма с ID {}", film.getId());
            throw new IdNotFoundException("Ошибка идентификации фильма. Не существует фильма с ID " + film.getId());
        }

        filmStorage.update(film);
        log.debug("Обновлен фильм: {}", film);

        return film;
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = findById(filmId);
        userService.findById(userId);
        Map<Integer, Emoji> assessmentMap = film.getLikes();

        if (assessmentMap == null) {
            assessmentMap = new HashMap<>();
        }

        assessmentMap.put(userId, Emoji.LIKE);
        film.setLikes(assessmentMap);

        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film film = findById(filmId);

        userService.findById(userId);
        Map<Integer, Emoji> assessmentMap = film.getLikes();
        assessmentMap.remove(userId);
        film.setLikes(assessmentMap);

        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {

        if (count <= 0) {
            log.error("Запрошено не корректное количество фильмов {}", count);
            throw new ValidationException("Запрошено не корректное количество фильмов: "
                    + count);
        }

        return filmStorage.topFilms(count);
    }

    public List<Genre> findAllGenres() {

        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(Integer id) {
        Genre genre = filmStorage.findGenreById(id);

        if (genre == null) {
            log.error("Жанр с переданным ID {} не существует", id);
            throw new IdNotFoundException("Не существует жанра с переданным ID: " + id);
        }

        return genre;
    }

    public List<Mpa> findAllMpa() {

        return filmStorage.findAllMpa();
    }

    public Mpa findMpaById(Integer id) {
        Mpa mpa = filmStorage.findMpaById(id);

        if (mpa == null) {
            log.error("MPA с переданным ID {} не существует", id);
            throw new IdNotFoundException("Не существует MPA с переданным ID " + id);
        }

        return mpa;
    }

}
