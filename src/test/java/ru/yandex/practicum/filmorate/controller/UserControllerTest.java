package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    @Test
    public void updateUserWhenIdIsNotExist() {
        User user1 = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        User user2 = new User(2, "updatedemail@leo.ru", "Updated login", "Updated name",
                LocalDate.parse("1995-12-25"));
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        inMemoryUserStorage.create(user1);

        Executable executable = () -> inMemoryUserStorage.update(user2);

        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, executable);
        assertEquals("Не существует пользвателя с ID " + user2.getId(),
                idNotFoundException.getMessage());
    }

    @Test
    public void addFriendByIdWhenIdsAreEqual() {
        User user1 = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        User user2 = new User(2, "updatedemail@leo.ru", "Updated login", "Updated name",
                LocalDate.parse("1995-12-25"));
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        inMemoryUserStorage.create(user1);
        inMemoryUserStorage.create(user2);

        Executable executable = () -> userService.addFriendById(1, 1);

        IdPassingException idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Переданы не корректные ID пользователей: 1, 1",
                idPassingException.getMessage());
    }

    @Test
    public void addFriendByIdWhenIdIsZero() {
        User user1 = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        User user2 = new User(2, "updatedemail@leo.ru", "Updated login", "Updated name",
                LocalDate.parse("1995-12-25"));
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        inMemoryUserStorage.create(user1);
        inMemoryUserStorage.create(user2);

        Executable executable = () -> userService.addFriendById(0, 1);

        IdPassingException idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Переданы не корректные ID пользователей: 0, 1",
                idPassingException.getMessage());
    }

    @Test
    public void deleteFriendByIdWhenIdsAreEqual() {
        User user1 = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        User user2 = new User(2, "updatedemail@leo.ru", "Updated login", "Updated name",
                LocalDate.parse("1995-12-25"));
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        inMemoryUserStorage.create(user1);
        inMemoryUserStorage.create(user2);

        Executable executable = () -> userService.deleteFriendById(1, 1);

        IdPassingException idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Переданы не корректные ID пользователей: 1, 1",
                idPassingException.getMessage());
    }

    @Test
    public void deleteFriendByIdWhenIdIsZero() {
        User user1 = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        User user2 = new User(2, "updatedemail@leo.ru", "Updated login", "Updated name",
                LocalDate.parse("1995-12-25"));
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        inMemoryUserStorage.create(user1);
        inMemoryUserStorage.create(user2);

        Executable executable = () -> userService.deleteFriendById(0, 1);

        IdPassingException idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Переданы не корректные ID пользователей: 0, 1",
                idPassingException.getMessage());
    }
}