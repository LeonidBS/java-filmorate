package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Emoji;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findById(filmId);

        userStorage.findById(userId);
        Map<Integer, Emoji> assessmentMap = film.getLikes();
        if (assessmentMap == null) {
            assessmentMap = new HashMap<>();
        }
        assessmentMap.put(userId, Emoji.LIKE);
        film.setLikes(assessmentMap);

        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findById(filmId);

        userStorage.findById(userId);
        Map<Integer, Emoji> assessmentMap = film.getLikes();
        assessmentMap.remove(userId);
        film.setLikes(assessmentMap);
        filmStorage.update(film);
        return film;
    }

    public List<Film> getTopFilms(Integer count) {

        if (count <= 0) {
            log.error("Запрошено не корректное количество фильмов {}", count);
            throw new ValidationException("Запрошено не корректное количество фильмов: "
                    + count);
        }

        return filmStorage.findAll().stream()
                .collect(Collectors.toMap(Film::getId, f -> f.getLikes().size()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(count)
                .map(f -> filmStorage.findById(f.getKey()))
                .collect(Collectors.toList());

    }
}
