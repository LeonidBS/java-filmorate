package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class Like {
    private final Integer userId;
    private final Emoji emoji = Emoji.LIKE;

    public Like(Integer userId) {
        this.userId = userId;
    }
}
