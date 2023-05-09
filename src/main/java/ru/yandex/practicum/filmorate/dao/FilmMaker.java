package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Emoji;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FilmMaker implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");

        String sql = "SELECT user_id FROM films_likes WHERE film_id=?";
        Map<Integer, Emoji> likes = jdbcTemplate.query(sql,
                        (rs1, rowNum1) -> rs1.getInt("user_id"), id)
                .stream()
                .collect(Collectors.toMap(Function.identity(), e -> Emoji.LIKE));

        sql = "SELECT g.id id, g.name name FROM genres g " +
                "INNER JOIN films_genres fg ON g.id = fg.genres_id " +
                "WHERE fg.film_id=?";
        List<Genre> genres = jdbcTemplate.query(sql, new Object[]{id},
                new BeanPropertyRowMapper<>(Genre.class));
        Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));

        return new Film(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                likes,
                mpa,
                genres);
    }
}

