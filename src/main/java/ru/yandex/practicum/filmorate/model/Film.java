package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validator.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Film {

    @PositiveOrZero
    private Integer id;

    @NotBlank(message = "Отсутсвует название фильма")
    private String name;

    @Size(max = 200, message = "Длина описания превышает максимально допустиму 200 символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @AfterDate(date = "1895-12-27", message = "Дата релиза поздее даты выпуска первого фильма")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность не положительная")
    private int duration;

   /*
    Задание с лайками выполнено с доп. функциональностью намерено
    */

    private Map<Integer, Emoji> likes = new HashMap<>();

    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();

    public Film(Integer id, String name, String description, LocalDate releaseDate,
                int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate,
                int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Film(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("releaseDate") LocalDate releaseDate,
                @JsonProperty("duration") int duration,
                @JsonProperty("likes") Map<Integer, Emoji> likes,
                @JsonProperty("mpa") Mpa mpa,
                @JsonProperty("genres") List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        if (likes == null) {
            this.likes = new HashMap<>();
        } else {
            this.likes = likes;
        }
        this.mpa = mpa;
        if (genres == null) {
            this.genres = new ArrayList<>();
        } else {
            this.genres = genres;
        }
    }


}

