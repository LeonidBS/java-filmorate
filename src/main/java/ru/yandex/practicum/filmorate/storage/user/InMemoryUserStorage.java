package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;
    private final Map<Friends, Boolean> allFriends = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Integer id) {
        User user = users.get(id);

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

        user.setId(id++);
        users.put(user.getId(), user);
        log.debug("Добавен пользователь: {}", user);
        return user;
    }

    public User update(User user) {

        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с переданным ID {} не существует", user.getId());
            throw new IdNotFoundException("Не существует пользвателя с ID " + user.getId());
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        log.debug("Обновлен пользователь: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Friends addFriends(Integer invitor, Integer invitee) {
        Friends friends = new Friends(invitor, invitee);
        if (allFriends.put(friends, true) != null) {
            log.error("Пользователи: " + users.get(invitor) + " и " + users.get(invitee) +
                    "  уже являются друзьями");
            throw new ValidationException("Пользователи: " + users.get(invitor) +
                    " и " + users.get(invitee) + "  уже являются друзьями");
        }
        return friends;
    }

    @Override
    public Friends deleteFriends(Integer invitor, Integer invitee) {
        Friends friends = new Friends(invitor, invitee);
        if (allFriends.remove(friends) != null) {
            log.debug("Пользователи с ID {}, {} удалены из друзей: ", invitor,invitee);
            return friends;
        } else {
            log.error("Друзей с ID {}, {} не существует", invitor,invitee);
            throw new IdPassingException("Не существует  друзей с ID :" +
                    invitor + ", " + invitee);
        }
    }

    @Override
    public Collection<Friends> findAllFriends() {
        return allFriends.keySet();
    }
}
