package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("mem")
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

        return users.get(id);
    }

    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);

        return user;
    }

    public User update(User user) {
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
            log.debug("Пользователи с ID {}, {} удалены из друзей: ", invitor, invitee);
            return friends;
        } else {
            log.error("Друзей с ID {}, {} не существует", invitor, invitee);
            throw new IdPassingException("Не существует  друзей с ID :" +
                    invitor + ", " + invitee);
        }
    }

    public List<User> findFriendsById(Integer id) {
        ;
        return allFriends.keySet().stream()
                .filter(friends -> friends.getInviter().equals(id)
                        | friends.getInvitee().equals(id))
                .map(friends -> {
                    if (friends.getInviter().equals(id)) {
                        return findById(friends.getInvitee());
                    } else {
                        return findById(friends.getInviter());
                    }
                })
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }

    public List<User> findMutualFriendsByTwoIds(Integer id, Integer friendId) {
        Friends friends = new Friends(id, friendId);

        return allFriends.keySet().stream()
                .filter(f -> (f.getInviter().equals(id) || f.getInvitee().equals(id))
                        || (f.getInviter().equals(friendId) || f.getInvitee().equals(friendId)))
                .filter(f -> !f.equals(friends))
                .map(f -> {
                    if (f.getInviter().equals(id) || f.getInviter().equals(friendId)) {
                        return findById(f.getInvitee());
                    } else {
                        return findById(f.getInviter());
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
