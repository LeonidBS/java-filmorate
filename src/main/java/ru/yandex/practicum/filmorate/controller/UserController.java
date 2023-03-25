package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationExcpretion;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final List<User> users = new ArrayList<>();

    @GetMapping
    public Collection<User> findAll() {
        return users;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(users.size());
        users.add(user);
        log.debug("Добавен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (users.size() <= user.getId()) {
            log.error("Пользователь с переданным ID {} не существует", user.getId());
            throw new ValidationExcpretion("Не существует пользвателя с ID " + user.getId());
        }

        users.set(user.getId(), user);
        log.debug("Обновлен пользователь: {}", user);
        return user;
    }
}

