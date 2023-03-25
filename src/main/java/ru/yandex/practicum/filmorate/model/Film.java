package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;
import ru.yandex.practicum.filmorate.validator.DurationIsPositive;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {

    @PositiveOrZero
    private int id;

    @NotNull(message = "Отсутсвует название фильма")
    @NotBlank(message = "Поле названия фильма пустое")
    private final String name;

    @Size(max = 200, message = "Длина описания превышает максимально допустиму 200 символов")
    private final String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Дата релиза указана в будущем")
    @AfterDate(date = "1895-12-27", message = "Дата релиза поздее даты выпуска первого фильма")
    private final LocalDate releaseDate;

    @Positive(message = "Продолжительность не положительная")
    private final int duration;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Film(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("releaseDate") LocalDate releaseDate,
                @JsonProperty("duration") int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}

