package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable String id) {
        try {
            return userService.findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("Переданый ID: {} не является целым числом", id);
            throw new IdPassingException(String.format("Переданый ID: %s не является целым числом", id));
        }
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Friends putNewFriend(@PathVariable String id, @PathVariable String friendId) {
        try {
            return userService.addFriendById(Integer.parseInt(id), Integer.parseInt(friendId));
        } catch (NumberFormatException e) {
            log.error("Один или оба переданных ID: {}, {} не являются целым числом", id, friendId);
            throw new IdPassingException(String.format("Один или оба переданных ID: %s," +
                    " %s не являются целым числом", id, friendId));
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Friends deleteFriend(@PathVariable String id, @PathVariable String friendId) {

        try {
            return userService.deleteFriendById(Integer.parseInt(id), Integer.parseInt(friendId));
        } catch (NumberFormatException e) {
            throw new IdPassingException(String.format("Один или оба переданных ID: %s," +
                    " %s не являются целым числом", id, friendId));
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable String id) {

        try {
            return userService.findFriendsById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            log.error("Переданый ID: {} не является целым числом", id);
            throw new IdPassingException(String.format("Переданый ID: %s не является целым числом",
                    id));
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {

        try {
            return userService.findMutualFriendsByTwoIds(Integer.parseInt(id), Integer.parseInt(otherId));
        } catch (NumberFormatException e) {
            throw new IdPassingException(String.format("Один или оба переданных ID: %s," +
                    " %s не являются целым числом", id, otherId));
        }
    }
}

