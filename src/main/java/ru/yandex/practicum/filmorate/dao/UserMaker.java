package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserMaker implements RowMapper<User> {


    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new User(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
    }
}
