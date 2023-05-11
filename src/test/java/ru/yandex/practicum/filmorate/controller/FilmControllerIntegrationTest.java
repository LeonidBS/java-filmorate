package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Emoji;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerIntegrationTest {
    private MockMvc mockMvc;
    private InMemoryFilmStorage inMemoryFilmStorage;
    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    private Mpa mpa = new Mpa(1, "NC-17");

    @BeforeEach
    public void setup() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(
                        new FilmService(inMemoryFilmStorage, userService)), new ErrorHandler())
                .build();
    }

    @Test
    public void createPostWhenFilmFieldsAreCorrect() throws Exception {
        Film film = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(film.getName()))
                .andExpect(jsonPath("$.description").value(film.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(film.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(film.getDuration()))
                .andReturn();
    }

    @Test
    public void createPostWhenNameIsNull() throws Exception {
        Film film = new Film(null, "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenNameIsEmpty() throws Exception {
        Film film = new Film("", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
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
        Film film = new Film(null, descriptionString.toString(),
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenReleaseDateIsBeforeReleaseOfFirstFilm() throws Exception {
        Film film = new Film(null, "description1",
                LocalDate.parse("1895-12-27"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenDurationIsNotPositive() throws Exception {
        Film film = new Film(null, "description1",
                LocalDate.parse("1995-12-27"), 1, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenFilmFieldsAreCorrect() throws Exception {
        Map<Integer, Emoji> likes = new HashMap<>();
        likes.put(1, Emoji.LIKE);
        likes.put(2, Emoji.LIKE);
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, likes,
                mpa, new ArrayList<>());
        Film film2 = new Film(1, "Updated name2", "Updated description2",
                LocalDate.parse("2001-03-01"), 115, likes,
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(film2.getName()))
                .andExpect(jsonPath("$.description").value(film2.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(film2.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(film2.getDuration()))
                .andExpect(jsonPath("likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(likes.toString())))
                .andReturn();
    }

    @Test
    public void updateFilmWhenIdIsNotExist() throws Exception {
        Film film1 = new Film("name", "description",
                LocalDate.parse("1995-12-27"), 90, new HashMap<>(),
                mpa, new ArrayList<>());
        Film film2 = new Film(2, "Updated name", "Updated description",
                LocalDate.parse("1995-12-27"), 95, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    public void updatePutWhenNameIsNull() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        Film film2 = new Film(1, null, "Updated description2",
                LocalDate.parse("2001-03-01"), 115, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenNameIsEmpty() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        Film film2 = new Film(1, "", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenDescriptionIsSizeMoreThan200() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        StringBuilder descriptionString = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            descriptionString.append("a");
        }
        Film film2 = new Film(1, null, descriptionString.toString(),
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenReleaseDateIsBeforeReleaseOfFirstFilm() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        Film film2 = new Film(1, null, "description1",
                LocalDate.parse("1895-12-27"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenDurationIsNotPositive() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        Film film2 = new Film(1, null, "description1",
                LocalDate.parse("1995-12-27"), 1, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getFilmListRequest() throws Exception {
        Film film = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(film.getName()))
                .andExpect(jsonPath("$[0].description").value(film.getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(film.getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(film.getDuration()))
                .andReturn();
    }

    @Test
    public void finByIdWhenIdIsExist() throws Exception {
        HashMap<Integer, Emoji> likes = new HashMap<>();
        likes.put(1, Emoji.LIKE);
        likes.put(2, Emoji.LIKE);
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, likes,
                mpa, new ArrayList<>());
        Film film2 = new Film("name2", "description2",
                LocalDate.parse("2001-03-01"), 115, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonFilm))
                .andReturn();
        jsonFilm = objectMapper.writeValueAsString(film2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonFilm))
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/films/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(film1.getName()))
                .andExpect(jsonPath("description").value(film1.getDescription()))
                .andExpect(jsonPath("releaseDate").value(film1.getReleaseDate().toString()))
                .andExpect(jsonPath("duration").value(film1.getDuration()))
                .andExpect(jsonPath("likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(likes.toString())))
                .andReturn();
    }

    @Test
    public void finByIdWhenIdIsNotInteger() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/films/q")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void finByIdWhenIdIsNotExist() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), 93, new HashMap<>(),
                mpa, new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonFilm))
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/films/2")
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
        objectMapper.findAndRegisterModules();
        HashMap<Integer, Emoji> likes = new HashMap<>();
        likes.put(2, Emoji.LIKE);
        likes.put(3, Emoji.LIKE);

        for (int i = 1; i < 3; i++) {
            jsonFilm = objectMapper.writeValueAsString(new Film("name" + i,
                    "description" + i, LocalDate.parse("1995-12-27").minusYears(i),
                    90 + i, new HashMap<>(),
                    mpa, new ArrayList<>()));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        for (int i = 1; i < 5; i++) {
            inMemoryUserStorage.create(new User("email@leo" + i + ".ru", "login" + i,
                    "name" + i, LocalDate.parse("1995-12-27").plusMonths(i)));
        }

        for (int j = 2; j < 4; j++) {
            this.mockMvc.perform(MockMvcRequestBuilders.put(String.format("/films/%d/like/%d", 2, j)))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        this.mockMvc.perform(MockMvcRequestBuilders.get(String.format("/films/2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(inMemoryFilmStorage.findById(2)
                        .getName()))
                .andExpect(jsonPath("description").value(inMemoryFilmStorage.findById(2)
                        .getDescription()))
                .andExpect(jsonPath("releaseDate").value(inMemoryFilmStorage.findById(2)
                        .getReleaseDate().toString()))
                .andExpect(jsonPath("duration").value(inMemoryFilmStorage.findById(2)
                        .getDuration()))
                .andExpect(jsonPath("likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(likes.toString())))
                .andReturn();
    }

    @Test
    public void addLikeWhenFilmIdAndUserIdIsNotInteger() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        for (int i = 1; i < 3; i++) {
            jsonFilm = objectMapper.writeValueAsString(new Film("name" + i,
                    "description" + i, LocalDate.parse("1995-12-27").minusYears(i),
                    90 + i, new HashMap<>(),
                    mpa, new ArrayList<>()));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        for (int i = 1; i < 5; i++) {
            inMemoryUserStorage.create(new User("email@leo" + i + ".ru", "login" + i,
                    "name" + i, LocalDate.parse("1995-12-27").plusMonths(i)));
        }

        this.mockMvc.perform(MockMvcRequestBuilders.put("/films/q/like/1.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getPopularCountIsCorrect() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        for (int i = 1; i < 3; i++) {
            jsonFilm = objectMapper.writeValueAsString(new Film("name" + i,
                    "description" + i, LocalDate.parse("1995-12-27").minusYears(i),
                    90 + i, new HashMap<>(),
                    mpa, new ArrayList<>()));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        for (int i = 1; i < 3; i++) {
            inMemoryUserStorage.create(new User("email@leo" + i + ".ru", "login" + i,
                    "name" + i, LocalDate.parse("1995-12-27").plusMonths(i)));
        }

        for (int i = 1; i < 3; i++) {
            for (int j = 1; j < i + 1; j++) {
                this.mockMvc.perform(MockMvcRequestBuilders.put(String.format("/films/%d/like/%d", i, j)))
                        .andExpect(status().isOk())
                        .andReturn();
            }
        }

        this.mockMvc.perform(MockMvcRequestBuilders.get("/films/popular/?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(inMemoryFilmStorage
                        .findById(2).getName()))
                .andExpect(jsonPath("$[0].description").value(inMemoryFilmStorage
                        .findById(2).getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(inMemoryFilmStorage
                        .findById(2).getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(inMemoryFilmStorage
                        .findById(2).getDuration()))
                .andExpect(jsonPath("$[0].likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(inMemoryFilmStorage.findById(2)
                        .getLikes().toString())))
                .andReturn();
    }

    @Test
    public void getPopularCountIsNotSpecified() throws Exception {
        String jsonFilm;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        for (int i = 1; i < 15; i++) {
            jsonFilm = objectMapper.writeValueAsString(new Film("name" + i,
                    "description" + i, LocalDate.parse("1995-12-27").minusYears(i),
                    90 + i, new HashMap<>(),
                    mpa, new ArrayList<>()));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonFilm))
                    .andReturn();
        }

        for (int i = 1; i < 17; i++) {
            inMemoryUserStorage.create(new User("email@leo" + i + ".ru", "login" + i,
                    "name" + i, LocalDate.parse("1995-12-27").plusMonths(i)));
        }

        for (int i = 2; i < 13; i++) {
            for (int j = 1; j < i; j++) {
                this.mockMvc.perform(MockMvcRequestBuilders.put(String.format("/films/%d/like/%d", i, j)))
                        .andExpect(status().isOk())
                        .andReturn();
            }
        }

        this.mockMvc.perform(MockMvcRequestBuilders.get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].name").value(inMemoryFilmStorage
                        .findById(12).getName()))
                .andExpect(jsonPath("$[0].description").value(inMemoryFilmStorage
                        .findById(12).getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(inMemoryFilmStorage
                        .findById(12).getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(inMemoryFilmStorage
                        .findById(12).getDuration()))
                .andExpect(jsonPath("$[0].likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(inMemoryFilmStorage.findById(12)
                        .getLikes().toString())))
                .andExpect(jsonPath("$[9].name").value(inMemoryFilmStorage
                        .findById(3).getName()))
                .andExpect(jsonPath("$[9].description").value(inMemoryFilmStorage
                        .findById(3).getDescription()))
                .andExpect(jsonPath("$[9].releaseDate").value(inMemoryFilmStorage
                        .findById(3).getReleaseDate().toString()))
                .andExpect(jsonPath("$[9].duration").value(inMemoryFilmStorage
                        .findById(3).getDuration()))
                .andExpect(jsonPath("$[9].likes", Matchers.<Map<Integer,
                        Emoji>>hasToString(inMemoryFilmStorage.findById(3)
                        .getLikes().toString())))
                .andReturn();
    }
}

