package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor

public class Film {

    @PositiveOrZero
    private Integer id;

    @NotBlank(message = "Отсутсвует название фильма")
    private final String name;

    @Size(max = 200, message = "Длина описания превышает максимально допустиму 200 символов")
    private final String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @AfterDate(date = "1895-12-27", message = "Дата релиза поздее даты выпуска первого фильма")
    private final LocalDate releaseDate;

    @Positive(message = "Продолжительность не положительная")
    private final int duration;

   /*
    Задание с лайками выполнено с доп. функциональностью намерено
    */

    private Map<Integer, Emoji> likes;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Film(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("releaseDate") LocalDate releaseDate,
                @JsonProperty("duration") int duration,
                @JsonProperty("likes") Map<Integer, Emoji> likes) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
    }
}

