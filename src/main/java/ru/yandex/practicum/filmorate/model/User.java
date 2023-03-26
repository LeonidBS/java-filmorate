package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    @PositiveOrZero
    private Integer id;

    @NotBlank(message = "Отсутсвует email")
    @Email(message = "Переданная строка не соответввует формату email")
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "Не корректный email")
    private final String email;

    @NotBlank(message = "Отсутсвует логин")
    @Pattern(regexp = "[^\\s]+", message = "В логине есть пробелы")
    private final String login;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Дата рождения в будущем")
    private final LocalDate birthday;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public User(@JsonProperty("email") String email,
                @JsonProperty("login") String login,
                @JsonProperty("name") String name,
                @JsonProperty("birthday") LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
