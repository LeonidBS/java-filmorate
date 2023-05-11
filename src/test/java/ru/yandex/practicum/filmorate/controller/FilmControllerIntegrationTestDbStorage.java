package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerIntegrationTestDbStorage {
    private MockMvc mockMvc;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;

    private final DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setName("filmoratetestdb")
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("dataForFilmControllerTest.sql")
            .continueOnError(true).build();

    private Mpa mpa = new Mpa();
    private List<Genre> genres = new ArrayList<>();
    Film newfilm = Film.builder()
            .name("name1")
            .description("description1")
            .releaseDate(LocalDate.now())
            .duration(93)
            .likes(new HashMap<>())
            .mpa(new Mpa(1, "G"))
            .genres(new ArrayList<>())
            .build();

    Film updatedFilmId1 = Film.builder()
            .id(1)
            .name("Updated name1")
            .description("Updated description1")
            .releaseDate(LocalDate.now())
            .duration(115)
            .likes(new HashMap<>())
            .mpa(new Mpa(1, "G"))
            .genres(new ArrayList<>())
            .build();

    @BeforeEach
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
        userDbStorage = new UserDbStorage(jdbcTemplate);
        UserService userService = new UserService(userDbStorage);

        mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(
                        new FilmService(filmDbStorage, userService)), new ErrorHandler())
                .build();

        updatedFilmId1.setLikes(new HashMap<>());
        updatedFilmId1.setMpa(new Mpa(1, "G"));
        updatedFilmId1.setGenres(new ArrayList<>());
    }

    @Test
    public void createFilmWhenFilmFieldsAreCorrect() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newfilm.getName()))
                .andExpect(jsonPath("$.description").value(newfilm.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(newfilm.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(newfilm.getDuration()))
                .andExpect(jsonPath("$.mpa").value(newfilm.getMpa()))
                .andExpect(jsonPath("genres", Matchers.<List<Genre>>hasToString(newfilm
                        .getGenres().toString())))
                .andReturn();
    }

    @Test
    public void createPostWhenNameIsNull() throws Exception {
        newfilm.setName(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenNameIsEmpty() throws Exception {
        newfilm.setName("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenDescriptionIsSizeMoreThan200() throws Exception {
        StringBuilder descriptionString = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            descriptionString.append("a");
        }
        newfilm.setDescription(descriptionString.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenReleaseDateIsBeforeReleaseOfFirstFilm() throws Exception {
        newfilm.setReleaseDate(LocalDate.parse("1895-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenDurationIsNotPositive() throws Exception {
        newfilm.setDuration(-1);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenMpaIsNotCorrect() throws Exception {
        Mpa notCorrectMpa = new Mpa(10, "G");
        newfilm.setMpa(notCorrectMpa);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Передан не корректный MPA: " + newfilm.getMpa(),
                        result.getResolvedException().getMessage()));
    }

    @Test
    public void updatePutWhenFilmFieldsAreCorrect() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();
        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedFilmId1.getName()))
                .andExpect(jsonPath("$.description").value(updatedFilmId1.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(updatedFilmId1.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(updatedFilmId1.getDuration()))
                .andExpect(jsonPath("likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(updatedFilmId1.getLikes().toString())))
                .andExpect(jsonPath("$.mpa").value(updatedFilmId1.getMpa()))
                .andExpect(jsonPath("genres", Matchers.<List<Genre>>hasToString(updatedFilmId1
                        .getGenres().toString())))
                .andReturn();
    }

    @Test
    public void updateFilmWhenIdIsNotExist() throws Exception {
        Film film2 = new Film(2, "Updated name", "Updated description",
                LocalDate.parse("1995-12-27"), 95, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film2);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void updatePutWhenNameIsNull() throws Exception {
        updatedFilmId1.setName(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenNameIsEmpty() throws Exception {
        updatedFilmId1.setName("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenDescriptionIsSizeMoreThan200() throws Exception {
        StringBuilder descriptionString = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            descriptionString.append("a");
        }
        updatedFilmId1.setDescription(descriptionString.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenReleaseDateIsBeforeReleaseOfFirstFilm() throws Exception {
        updatedFilmId1.setReleaseDate(LocalDate.parse("1895-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenDurationIsNotPositive() throws Exception {
        updatedFilmId1.setDuration(-1);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenFilmGenreIsNotCorrect() throws Exception {
        Genre genre = new Genre(11, "Кошмары");
        genres.add(genre);
        updatedFilmId1.setGenres(genres);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof ValidationException));
    }

    @Test
    public void findAllFilmsRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(updatedFilmId1.getName()))
                .andExpect(jsonPath("$[0].description").value(updatedFilmId1.getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(updatedFilmId1.getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(updatedFilmId1.getDuration()))
                .andExpect(jsonPath("[0].likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(updatedFilmId1.getLikes().toString())))
                .andExpect(jsonPath("$[0].mpa").value(updatedFilmId1.getMpa()))
                .andExpect(jsonPath("[0].genres", Matchers.<List<Genre>>hasToString(updatedFilmId1
                        .getGenres().toString())))
                .andReturn();
    }

    @Test
    public void findFilmByIdWhenIdIsExist() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.get("/films/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedFilmId1.getName()))
                .andExpect(jsonPath("$.description").value(updatedFilmId1.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(updatedFilmId1.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(updatedFilmId1.getDuration()))
                .andExpect(jsonPath("likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(updatedFilmId1.getLikes().toString())))
                .andExpect(jsonPath("$.mpa").value(updatedFilmId1.getMpa()))
                .andExpect(jsonPath("genres", Matchers.<List<Genre>>hasToString(updatedFilmId1
                        .getGenres().toString())))
                .andReturn();
    }

    @Test
    public void finByIdWhenIdIsNotInteger() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/films/q")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void finByIdWhenIdIsNotExist() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(newfilm);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonFilm))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.get("/films/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdNotFoundException))
                .andExpect(result -> assertEquals("Не существует фильма с ID: 2",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void addLikeWhenFilmIdAndUserIdIsExist() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Integer, Emoji> likes1 = new HashMap<>();
        likes1.put(1, Emoji.LIKE);
        Map<Integer, Emoji> likes2 = new HashMap<>();
        likes2.put(1, Emoji.LIKE);
        likes2.put(2, Emoji.LIKE);
        objectMapper.findAndRegisterModules();
        Genre genre = new Genre(1, "Комедия");
        genres.add(genre);
        updatedFilmId1.setGenres(genres);
        jsonFilm = objectMapper.writeValueAsString(updatedFilmId1);
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonFilm))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/films/%d/like/%d", 1, 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(filmDbStorage.findById(1)
                        .getName()))
                .andExpect(jsonPath("description").value(filmDbStorage.findById(1)
                        .getDescription()))
                .andExpect(jsonPath("releaseDate").value(filmDbStorage.findById(1)
                        .getReleaseDate().toString()))
                .andExpect(jsonPath("duration").value(filmDbStorage.findById(1)
                        .getDuration()))
                .andExpect(jsonPath("likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(likes1.toString())))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/films/%d/like/%d", 1, 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(filmDbStorage.findById(1)
                        .getName()))
                .andExpect(jsonPath("description").value(filmDbStorage.findById(1)
                        .getDescription()))
                .andExpect(jsonPath("releaseDate").value(filmDbStorage.findById(1)
                        .getReleaseDate().toString()))
                .andExpect(jsonPath("duration").value(filmDbStorage.findById(1)
                        .getDuration()))
                .andExpect(jsonPath("likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(likes2.toString())))
                .andReturn();
    }

    @Test
    public void addLikeWhenFilmIdAndUserIdIsNotInteger() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        for (int i = 1; i < 3; i++) {
            jsonFilm = objectMapper.writeValueAsString(
                    Film.builder()
                            .name("name" + i)
                            .description("description" + i)
                            .releaseDate(LocalDate.now().minusYears(i))
                            .duration(90 + i)
                            .likes(new HashMap<>())
                            .mpa(new Mpa(1, "G"))
                            .genres(new ArrayList<>())
                            .build());
            mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        mockMvc.perform(MockMvcRequestBuilders.put("/films/q/like/1.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void addLikeWhenFilmIdIsNotExist() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        for (int i = 1; i < 3; i++) {
            jsonFilm = objectMapper.writeValueAsString(
                    Film.builder()
                            .name("name" + i)
                            .description("description" + i)
                            .releaseDate(LocalDate.now().minusYears(i))
                            .duration(90 + i)
                            .likes(new HashMap<>())
                            .mpa(new Mpa(1, "G"))
                            .genres(new ArrayList<>())
                            .build());
            mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        mockMvc.perform(MockMvcRequestBuilders.put("/films/999/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addLikeWhenUserIdIsNotExist() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        for (int i = 1; i < 3; i++) {
            jsonFilm = objectMapper.writeValueAsString(
                    Film.builder()
                            .name("name" + i)
                            .description("description" + i)
                            .releaseDate(LocalDate.now().minusYears(i))
                            .duration(90 + i)
                            .likes(new HashMap<>())
                            .mpa(new Mpa(1, "G"))
                            .genres(new ArrayList<>())
                            .build());
            mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getPopularCountIsCorrect() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        mpa.setId(1);
        mpa.setName("G");
        for (int i = 1; i < 3; i++) {
            jsonFilm = objectMapper.writeValueAsString(
                    Film.builder()
                            .name("name" + i)
                            .description("description" + i)
                            .releaseDate(LocalDate.now().minusYears(i))
                            .duration(90 + i)
                            .likes(new HashMap<>())
                            .mpa(new Mpa(1, "G"))
                            .genres(new ArrayList<>())
                            .build());
            mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        for (int i = 1; i < 3; i++) {
            for (int j = 1; j < i + 1; j++) {
                mockMvc.perform(MockMvcRequestBuilders.put(String.format("/films/%d/like/%d", i, j)))
                        .andExpect(status().isOk())
                        .andReturn();
            }
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/films/popular/?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(filmDbStorage
                        .findById(2).getName()))
                .andExpect(jsonPath("$[0].description").value(filmDbStorage
                        .findById(2).getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(filmDbStorage
                        .findById(2).getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(filmDbStorage
                        .findById(2).getDuration()))
                .andExpect(jsonPath("$[0].likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(filmDbStorage.findById(2)
                        .getLikes().toString())))
                .andExpect(jsonPath("$[0].mpa").value(filmDbStorage
                        .findById(2).getMpa()))
                .andExpect(jsonPath("$[0].genres", Matchers.<Map<Integer,
                        Genre>>hasToString(filmDbStorage.findById(2)
                        .getGenres().toString())))
                .andReturn();
    }

    @Test
    public void getPopularCountIsNotSpecified() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        mpa.setId(1);
        mpa.setName("G");
        for (int i = 1; i < 15; i++) {
            jsonFilm = objectMapper.writeValueAsString(
                    Film.builder()
                            .name("name" + i)
                            .description("description" + i)
                            .releaseDate(LocalDate.now().minusYears(i))
                            .duration(90 + i)
                            .likes(new HashMap<>())
                            .mpa(new Mpa(1, "G"))
                            .genres(new ArrayList<>())
                            .build());
            mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        for (int i = 1; i < 17; i++) {
            userDbStorage.create(User.builder()
                    .email("email@leo" + i + ".ru")
                    .login("login" + i)
                    .name("name" + i)
                    .birthday(LocalDate.parse("1995-12-27").plusMonths(i))
                    .build());
        }

        for (int i = 2; i < 13; i++) {
            for (int j = 1; j < i; j++) {
                mockMvc.perform(MockMvcRequestBuilders.put(String.format("/films/%d/like/%d", i, j)))
                        .andExpect(status().isOk())
                        .andReturn();
            }
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].name").value(filmDbStorage
                        .findById(12).getName()))
                .andExpect(jsonPath("$[0].description").value(filmDbStorage
                        .findById(12).getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(filmDbStorage
                        .findById(12).getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(filmDbStorage
                        .findById(12).getDuration()))
                .andExpect(jsonPath("$[0].likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(filmDbStorage.findById(12)
                        .getLikes().toString())))
                .andExpect(jsonPath("$[9].name").value(filmDbStorage
                        .findById(3).getName()))
                .andExpect(jsonPath("$[9].description").value(filmDbStorage
                        .findById(3).getDescription()))
                .andExpect(jsonPath("$[9].releaseDate").value(filmDbStorage
                        .findById(3).getReleaseDate().toString()))
                .andExpect(jsonPath("$[9].duration").value(filmDbStorage
                        .findById(3).getDuration()))
                .andExpect(jsonPath("$[9].likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(filmDbStorage.findById(3)
                        .getLikes().toString())))
                .andReturn();
    }

    @Test
    public void findAllGenresRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc.perform(MockMvcRequestBuilders.get("/genres"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Комедия"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Драма"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Мультфильм"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("Триллер"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("Документальный"))
                .andExpect(jsonPath("$[5].id").value(6))
                .andExpect(jsonPath("$[5].name").value("Боевик"))
                .andReturn();
    }

    @Test
    public void findGenreByIdWhenIdIsExist() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc.perform(MockMvcRequestBuilders.get("/genres/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Комедия"))
                .andReturn();
    }

    @Test
    public void findGenreByIdWhenIdIsNotExist() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc.perform(MockMvcRequestBuilders.get("/genres/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IdNotFoundException))
                .andExpect(result -> assertEquals("Не существует жанра с переданным ID: " + 999,
                        result.getResolvedException().getMessage()));
    }

    @Test
    public void findMpaGenresRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc.perform(MockMvcRequestBuilders.get("/mpa"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("PG"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("PG-13"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("R"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("NC-17"))
                .andReturn();
    }

    @Test
    public void findMpaByIdWhenIdIsExist() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc.perform(MockMvcRequestBuilders.get("/mpa/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("G"))
                .andReturn();
    }

    @Test
    public void findMpaByIdWhenIdIsNotExist() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc.perform(MockMvcRequestBuilders.get("/mpa/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IdNotFoundException))
                .andExpect(result -> assertEquals("Не существует MPA с переданным ID " + 999,
                        result.getResolvedException().getMessage()));
    }
}

