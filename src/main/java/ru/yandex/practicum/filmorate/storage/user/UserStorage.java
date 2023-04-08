package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Component
public interface UserStorage {
    List<User> findAll();

    User findById(Integer id);

    User create(User user);

    User update(User user);

    Friends addFriends(Integer invitor, Integer invitee);

    Friends deleteFriends(Integer invitor, Integer invitee);

    Collection<Friends> findAllFriends();
}
