package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Emoji;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("mem")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    private final Map<Integer, Genre> genres = new HashMap<>();

    private final Map<Integer, Mpa> mpa = new HashMap<>();
    private int id = 1;

    @Override
    public List<Film> findAll() {

        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Integer id) {

        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public List<Film> topFilms(Integer count) {

        return films.values().stream()
                .collect(Collectors.toMap(Film::getId, f -> f.getLikes().size()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(count)
                .map(f -> films.get(f.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        Film film = findById(filmId);
        Map<Integer, Emoji> assessmentMap = film.getLikes();

        if (assessmentMap == null) {
            assessmentMap = new HashMap<>();
        }

        assessmentMap.put(userId, Emoji.LIKE);
        film.setLikes(assessmentMap);
        return films.get(filmId);
    }

    @Override
    public Film removeLike(Integer filmId, Integer userId) {
        Film film = findById(filmId);
        Map<Integer, Emoji> assessmentMap = film.getLikes();
        assessmentMap.remove(userId);
        update(film);

        return films.get(filmId);
    }

    @Override
    public List<Genre> findAllGenres() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Genre findGenreById(Integer id) {
        return genres.get(id);
    }

    @Override
    public List<Mpa> findAllMpa() {
        return new ArrayList<>(mpa.values());
    }

    @Override
    public Mpa findMpaById(Integer id) {
        return mpa.get(id);
    }
}
