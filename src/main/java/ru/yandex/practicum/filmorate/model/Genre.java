package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class Genre {
    private Integer id;

    @Size(max = 50, message = "Длина наименования жанра превышает максимально допустиму 50 символов")
    private String name;

    public Genre() {
    }

    public Genre(Integer id) {
        this.id = id;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Genre(@JsonProperty("id") Integer id,
                 @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}