package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTest {
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController())
                .build();
    }

    @Test
    public void createPostWhenUserFieldsAreCorrect() throws Exception {
        User user = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.birthday").value(user.getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void createPostWhenEmailIsNull() throws Exception {
        User user = new User(null, "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenEmailIsEmpty() throws Exception {
        User user = new User("", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenEmailWithOutAtSign() throws Exception {
        User user = new User("emailleo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenEmailHasUnacceptableSymbols() throws Exception {
        User user = new User("ema?il@le o.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenLoginIsNull() throws Exception {
        User user = new User("email@leo.ru", null, "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenLoginIsNEmpty() throws Exception {
        User user = new User("email@leo.ru", "", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenLoginHasWhiteSpaces() throws Exception {
        User user = new User("email@leo.ru", "lo gi n", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void createPostWhenBirthdayInFuture() throws Exception {
        User user = new User("email@leo.ru", "login", "name",
                LocalDate.now().plusDays(1));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenUserFieldsAreCorrect() throws Exception {
        User user1 = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        User user2 = new User(0, "updatedemail@leo.ru", "updatedlogin", "Updated name",
                LocalDate.parse("1999-12-25"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user2.getEmail()))
                .andExpect(jsonPath("$.login").value(user2.getLogin()))
                .andExpect(jsonPath("$.name").value(user2.getName()))
                .andExpect(jsonPath("$.birthday").value(user2.getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailIsNull() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User(null, "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailIsEmpty() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User("", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailWithOutAtSign() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User("emailleo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailHasUnacceptableSymbols() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User("ema?il@le o.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenLoginIsNull() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User("email@leo.ru", null, "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenLoginIsNEmpty() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User("email@leo.ru", "", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenLoginHasWhiteSpaces() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User("email@leo.ru", "lo gi n", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updatePutWhenBirthdayInFuture() throws Exception {
        User user1 = new User("email1@leo.ru", "Original login", "Original name",
                LocalDate.parse("1990-12-27"));
        User user2 = new User("email@leo.ru", "login", "name",
                LocalDate.now().plusDays(1));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getUserListRequest() throws Exception {
        User user1 = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[0].login").value(user1.getLogin()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].birthday").value(user1.getBirthday().toString()))
                .andReturn();
    }
}