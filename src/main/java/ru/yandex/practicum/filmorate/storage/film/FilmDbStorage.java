package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmMaker;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("db")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query("SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration," +
                        "m.id as mpa_id, " +
                        "m.name as mpa_name " +
                        "FROM films f " +
                        "INNER JOIN mpa m ON f.mpa_id=m.id",
                new FilmMaker(jdbcTemplate));
    }

    @Override
    public Film findById(Integer id) {
        String sql = "SELECT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration," +
                "m.id as mpa_id, " +
                "m.name as mpa_name " +
                "FROM films f " +
                "INNER JOIN mpa m ON f.mpa_id=m.id " +
                "WHERE f.id=?";
        return jdbcTemplate.query(sql,
                        new Object[]{id}, new FilmMaker(jdbcTemplate))
                .stream().findAny().orElse(null);
    }

    @Override
    public Film create(Film film) {
        genreIsValid(film);
        Integer mpaId = mpaIdFind(film);
        if (mpaId != null) {
            String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), mpaId);
        } else {
            log.error("Передан не корректный MPA: " + film.getGenres());
            throw new ValidationException("Передан не корректный MPA: " + film.getMpa());
        }

        film.setId(jdbcTemplate.query("SELECT MAX(id) AS last_id FROM films",
                        (rs, rowNum) -> rs.getInt("last_id"))
                .stream()
                .findAny().orElse(0));

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO films_genres (genres_id, " +
                    "film_id) VALUES (?, ?)", genre.getId(), film.getId());
        }

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) {
        List<Integer> likesFromDb = jdbcTemplate.query("SELECT user_id " +
                        "FROM films_likes " +
                        "WHERE film_id = ?" +
                        "ORDER BY user_id",
                (rs, rowNum) -> rs.getInt("user_id"), film.getId());
        List<Integer> likesFromQuery = new ArrayList<>(film.getLikes().keySet());
        Collections.sort(likesFromQuery);

        if (!likesFromQuery.equals(likesFromDb)) {
            log.error("Переданные лайки: " + likesFromQuery +
                    " не соответствуют сохраненным в базе данных : " + likesFromDb);
            throw new ValidationException("Переданные лайки: " + likesFromQuery +
                    " не соответствуют сохраненным в базе данных : " + likesFromDb);
        }

        genreIsValid(film);
        Integer mpaId = mpaIdFind(film);

        if (mpaId != null) {
            String sql = "UPDATE films SET name=?, description=?, " +
                    " release_date=?, duration=?, mpa_id=? WHERE id = ?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(),
                    mpaId, film.getId());
        } else {
            log.error("Передан не корректный MPA: " + film.getGenres());
            throw new ValidationException("Передан не корректный MPA: " + film.getGenres());
        }

        List<Integer> genresIds = new ArrayList<>();
        List<Genre> genres = film.getGenres()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        film.setGenres(genres);
        for (Genre genre : genres) {
            Boolean isGenreIdExist = jdbcTemplate.query("SELECT COUNT(*)>0 AS isGenreId " +
                                    "FROM films_genres " +
                                    "WHERE film_id=? AND genres_id=?",
                            (rs, rowNum) -> rs.getBoolean("isGenreId"),
                            film.getId(), genre.getId())
                    .stream()
                    .findAny().orElse(false);
            if (!isGenreIdExist) {
                jdbcTemplate.update("INSERT INTO films_genres (genres_id, " +
                        "film_id) VALUES (?, ?)", genre.getId(), film.getId());
            }
            genresIds.add(genre.getId());
        }

        List<Integer> existGenresIds = jdbcTemplate.query("SELECT genres_id " +
                        "FROM films_genres " +
                        "WHERE film_id=?",
                (rs, rowNum) -> rs.getInt("genres_id"), film.getId());
        for (Integer id : existGenresIds) {
            if (!genresIds.contains(id))
                jdbcTemplate.update("DELETE FROM films_genres " +
                        "WHERE film_id=? AND genres_id=?", film.getId(), id);
        }
        return findById(film.getId());
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("INSERT INTO films_likes (film_id, " +
                "user_id, emoji) VALUES (?, ?, 'LIKE')", filmId, userId);

        return findById(filmId);
    }

    @Override
    public Film removeLike(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE FROM films_likes " +
                "WHERE film_id=? AND user_id=?", filmId, userId);

        return findById(filmId);
    }

    @Override
    public List<Film> topFilms(Integer count) {
        String sql = "SELECT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration," +
                "m.id as mpa_id, " +
                "m.name as mpa_name " +
                "FROM films f " +
                "INNER JOIN mpa m ON f.mpa_id=m.id " +
                "LEFT JOIN FILMS_LIKES fl on f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{count},
                new FilmMaker(jdbcTemplate));
    }

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres",
                new BeanPropertyRowMapper<>(Genre.class));
    }

    @Override
    public Genre findGenreById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM genres " +
                                "WHERE id=?",
                        new Object[]{id}, new BeanPropertyRowMapper<>(Genre.class))
                .stream().findAny().orElse(null);
    }

    @Override
    public List<Mpa> findAllMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa",
                new BeanPropertyRowMapper<>(Mpa.class));
    }

    @Override
    public Mpa findMpaById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM mpa " +
                                "WHERE id=?",
                        new Object[]{id}, new BeanPropertyRowMapper<>(Mpa.class))
                .stream().findAny().orElse(null);
    }


    private Integer mpaIdFind(Film film) {
        return jdbcTemplate.query("SELECT id FROM mpa " +
                                "WHERE id = ?",
                        (rs, rowNum) -> rs.getInt("id"), film.getMpa().getId())
                .stream()
                .findAny().orElse(null);
    }

    private Boolean genreIsValid(Film film) {
        for (Genre genre : film.getGenres()) {
            if (findGenreById(genre.getId()) == null) {
                log.error("Передан не корректный жанр: " + genre);
                throw new ValidationException("Передан не корректный жанр: " + genre);
            }
        }
        return true;
    }

}