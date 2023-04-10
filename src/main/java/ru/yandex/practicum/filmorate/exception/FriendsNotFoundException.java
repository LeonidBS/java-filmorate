package ru.yandex.practicum.filmorate.exception;

public class FriendsNotFoundException extends RuntimeException {
    public FriendsNotFoundException(String message) {
        super(message);
    }
}
