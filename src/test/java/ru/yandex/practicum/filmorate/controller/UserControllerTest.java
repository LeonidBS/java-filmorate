package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationExcpretion;
import ru.yandex.practicum.filmorate.model.User;

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
        UserController userController = new UserController();
        userController.create(user1);

        Executable executable = () -> userController.update(user2);

        ValidationExcpretion validationExcpretion = assertThrows(ValidationExcpretion.class, executable);
        assertEquals("Не существует пользвателя с ID " + user2.getId(),
                validationExcpretion.getMessage());
    }
}