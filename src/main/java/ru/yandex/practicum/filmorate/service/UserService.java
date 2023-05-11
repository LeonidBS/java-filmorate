package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@Component
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("db") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Integer id) {
        User user = userStorage.findById(id);

        if (user == null) {
            log.error("Пользователь с переданным ID {} не существует", id);
            throw new IdNotFoundException("Не существует пользвателя с ID: " + id);
        }

        return user;
    }

    public User create(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        log.debug("Добавен пользователь: {}", user);

        return userStorage.create(user);
    }

    public User update(User user) {

        if (userStorage.findById(user.getId()) == null) {
            log.error("Пользователь с переданным ID {} не существует", user.getId());
            throw new IdNotFoundException("Не существует пользвателя с ID " + user.getId());
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.update(user);
        log.debug("Обновлен пользователь: {}", user);

        return user;
    }

    public Friends addFriendById(Integer id, Integer friendId) {

        if (id.equals(friendId)) {
            log.error("Переданы не корректные ID пользователей: {}, {}", id, friendId);
            throw new IdPassingException("Переданы не корректные ID пользователей: "
                    + id + ", " + friendId);
        }

        findById(id);
        findById(friendId);
        return (userStorage.addFriends(id, friendId));
    }

    public Friends deleteFriendById(Integer id, Integer friendId) {

        if (id.equals(friendId) || id <= 0 || friendId <= 0) {
            log.error("Переданы не корректные ID пользователей: {}, {}", id, friendId);
            throw new IdPassingException("Переданы не корректные ID пользователей: "
                    + id + ", " + friendId);
        }

        findById(id);
        findById(friendId);

        return userStorage.deleteFriends(id, friendId);
    }

    public List<User> findFriendsById(Integer id) {
        userStorage.findById(id);
        return userStorage.findFriendsById(id);
    }

    public List<User> findMutualFriendsByTwoIds(Integer id, Integer friendId) {
        userStorage.findById(id);
        userStorage.findById(friendId);

        return userStorage.findMutualFriendsByTwoIds(id, friendId);
    }

}
