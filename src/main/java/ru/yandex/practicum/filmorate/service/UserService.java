package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Friends addFriendById(Integer id, Integer friendId) {

        if (id.equals(friendId)) {
            log.error("Переданы не корректные ID пользователей: {}, {}", id, friendId);
            throw new IdPassingException("Переданы не корректные ID пользователей: "
                    + id + ", " + friendId);
        }

        userStorage.findById(id);
        userStorage.findById(friendId);
        return (userStorage.addFriends(id, friendId));
    }

    public Friends deleteFriendById(Integer id, Integer friendId) {

        if (id.equals(friendId) || id <= 0 || friendId <= 0) {
            log.error("Переданы не корректные ID пользователей: {}, {}", id, friendId);
            throw new IdPassingException("Переданы не корректные ID пользователей: "
                    + id + ", " + friendId);
        }

        return userStorage.deleteFriends(id, friendId);
    }

    public List<User> findFriendsById(Integer id) {
        userStorage.findById(id);
        return userStorage.findAllFriends().stream()
                .filter(friends -> friends.getInviter().equals(id)
                        | friends.getInvitee().equals(id))
                .map(friends -> {
                    if (friends.getInviter().equals(id)) {
                        return userStorage.findById(friends.getInvitee());
                    } else {
                        return userStorage.findById(friends.getInviter());
                    }
                })
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }

    public List<User> findMutualFriendsByTwoIds(Integer id, Integer friendId) {
        userStorage.findById(id);
        userStorage.findById(friendId);
        Friends friends = new Friends(id, friendId);

        return userStorage.findAllFriends().stream()
                .filter(f -> (f.getInviter().equals(id) || f.getInvitee().equals(id))
                        || (f.getInviter().equals(friendId) || f.getInvitee().equals(friendId)))
                .filter(f -> !f.equals(friends))
                .map(f -> {
                    if (f.getInviter().equals(id) || f.getInviter().equals(friendId)) {
                        return userStorage.findById(f.getInvitee());
                    } else {
                        return userStorage.findById(f.getInviter());
                    }
                })
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(Function.identity()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 2)
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }
}
