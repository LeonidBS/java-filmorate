package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerIntegrationTest {
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FilmController())
                .build();
    }

    @Test
    public void createPostWhenFilmFieldsAreCorrect() throws Exception {
        Film film = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
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
                .andExpect(jsonPath("$.duration").value(film.getDuration().getSeconds()))
                .andReturn();
    }

    @Test
    public void createPostWhenNameIsNull() throws Exception {
        Film film = new Film(null, "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenNameIsEmpty() throws Exception {
        Film film = new Film("", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenDescriptionIsSizeMoreThan200() throws Exception {
        StringBuilder descriptionString = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            descriptionString.append("a");
        }
        Film film = new Film(null, descriptionString.toString(),
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenReleaseDateIsBeforeReleaseOfFirstFilm() throws Exception {
        Film film = new Film(null, "description1",
                LocalDate.parse("1895-12-27"), Duration.parse("PT1H33M"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenDurationIsNotPositive() throws Exception {
        Film film = new Film(null, "description1",
                LocalDate.parse("1995-12-27"), Duration.parse("PT-1M"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenFilmFieldsAreCorrect() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        Film film2 = new Film(0, "Updated name2", "Updated description2",
                LocalDate.parse("2001-03-01"), Duration.parse("PT1H55M"));
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
                .andExpect(jsonPath("$.duration").value(film2.getDuration().getSeconds()))
                .andReturn();
    }

    @Test
    public void updatePutWhenNameIsNull() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        Film film2 = new Film(1, null, "Updated description2",
                LocalDate.parse("2001-03-01"), Duration.parse("PT1H55M"));
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
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenNameIsEmpty() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        Film film2 = new Film(1, "", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
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
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenDescriptionIsSizeMoreThan200() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        StringBuilder descriptionString = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            descriptionString.append("a");
        }
        Film film2 = new Film(1, null, descriptionString.toString(),
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
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
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenReleaseDateIsBeforeReleaseOfFirstFilm() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        Film film2 = new Film(1, null, "description1",
                LocalDate.parse("1895-12-27"), Duration.parse("PT1H33M"));
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
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenDurationIsNotPositive() throws Exception {
        Film film1 = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
        Film film2 = new Film(1, null, "description1",
                LocalDate.parse("1995-12-27"), Duration.parse("PT-1M"));
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
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getFilmListRequest() throws Exception {
        Film film = new Film("name1", "description1",
                LocalDate.parse("2011-03-01"), Duration.parse("PT1H33M"));
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
                .andExpect(jsonPath("$[0].duration").value(film.getDuration().getSeconds()))
                .andReturn();
    }
}