package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Objects;

import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTest {
    private MockMvc mockMvc;
    private InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    public void setup() {
        inMemoryUserStorage = new InMemoryUserStorage();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(inMemoryUserStorage,
                        new UserService(inMemoryUserStorage)))
                .build();
    }

    @Test
    public void createPostWhenUserFieldsAreCorrect() throws Exception {
        User user = new User("email@leo.ru", "login", "name",
                LocalDate.parse("1995-12-27"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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
        User user2 = new User(1, "updatedemail@leo.ru", "updatedlogin", "Updated name",
                LocalDate.parse("1999-12-25"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonFilm = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andDo(print())
                .andReturn();

        jsonFilm = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonFilm))
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[0].login").value(user1.getLogin()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].birthday").value(user1.getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void finByIdWhenIdIsExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("email")
                        .value(inMemoryUserStorage.findById(1).getEmail()))
                .andExpect(jsonPath("login")
                        .value(inMemoryUserStorage.findById(1).getLogin()))
                .andExpect(jsonPath("name")
                        .value(inMemoryUserStorage.findById(1).getName()))
                .andExpect(jsonPath("birthday")
                        .value(inMemoryUserStorage.findById(1).getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void finByIdWhenIdIsNotInteger() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/q")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdPassingException))
                .andExpect(result -> assertEquals("Переданый ID: q не является целым числом",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void finByIdWhenIdIsNotExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdNotFoundException))
                .andExpect(result -> assertEquals("Не существует пользвателя с ID: 3",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void putNewFriendWhenUsersIdsAreExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }

        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("inviter")
                        .value(1))
                .andExpect(jsonPath("invitee")
                        .value(2))
                .andReturn();
    }

    @Test
    public void putNewFriendWhenFriendsAreAlreadyAdded() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }

        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/2/friends/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Пользователи: " + inMemoryUserStorage.findById(2) +
                                " и " + inMemoryUserStorage.findById(1) + "  уже являются друзьями",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void putNewFriendWhenUsersIdsAreNotInteger() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/q/friends/1.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdPassingException))
                .andExpect(result -> assertEquals("Один или оба переданных ID: q," +
                                " 1.0 не являются целым числом",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void deleteNewFriendWhenIdsAreExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void deleteNewFriendWhenIdsAreNotInteger() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/users/q/friends/1.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdPassingException))
                .andExpect(result -> assertEquals("Один или оба переданных ID: q," +
                                " 1.0 не являются целым числом",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void getFriendByIdWhenIdsAreExistAndFriendAreExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email")
                        .value(inMemoryUserStorage.findById(2).getEmail()))
                .andExpect(jsonPath("$[0].login")
                        .value(inMemoryUserStorage.findById(2).getLogin()))
                .andExpect(jsonPath("$[0].name")
                        .value(inMemoryUserStorage.findById(2).getName()))
                .andExpect(jsonPath("$[0].birthday")
                        .value(inMemoryUserStorage.findById(2).getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void getFriendByIdWhenIdsAreExistAndFriendAreNotExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()))
                .andReturn();
    }

    @Test
    public void getFriendByIdWhenIdIsNotInteger() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/q/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdPassingException))
                .andExpect(result -> assertEquals("Переданый ID: q не является целым числом",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void getCommonFriendByTwoIdsWhenIdsAreExistAndFriendAreExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 4; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(new User(i + "email1@leo.ru", "login" + i, "name" + i,
                    LocalDate.parse("1990-12-27").plusYears(i)));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn();
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/3"))
                .andExpect(status().isOk())
                .andReturn();
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/3/friends/2"))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email")
                        .value(inMemoryUserStorage.findById(3).getEmail()))
                .andExpect(jsonPath("$[0].login")
                        .value(inMemoryUserStorage.findById(3).getLogin()))
                .andExpect(jsonPath("$[0].name")
                        .value(inMemoryUserStorage.findById(3).getName()))
                .andExpect(jsonPath("$[0].birthday")
                        .value(inMemoryUserStorage.findById(3).getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void getCommonFriendsByTwoIdsWhenIdsAreNotInteger() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/q/friends/common/1.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdPassingException))
                .andExpect(result -> assertEquals("Один или оба переданных ID: q," +
                        " 1.0 не являются целым числом", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

}