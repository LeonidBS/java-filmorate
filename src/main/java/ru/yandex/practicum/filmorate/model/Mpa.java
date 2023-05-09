package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Mpa {
    private Integer id;
    private String name;

    public Mpa() {
    }

    public Mpa(Integer id) {
        this.id = id;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Mpa(@JsonProperty("id") Integer id,
               @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}