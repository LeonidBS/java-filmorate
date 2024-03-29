package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.yandex.practicum.filmorate.exception.IdPassingException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Objects;

import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIntegrationTestDbStorage {
    private MockMvc mockMvc;
    private UserDbStorage userDbStorage;
    private final DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setName("filmoratetestdb")
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql")
            .continueOnError(true).build();
    User user;

    @BeforeEach
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        userDbStorage = new UserDbStorage(jdbcTemplate);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(
                        new UserService(userDbStorage)), new ErrorHandler())
                .build();

        user = User.builder()
                .email("login@mail.ll")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
    }

    @Test
    public void createPostWhenUserFieldsAreCorrect() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
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
        user = User.builder()
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenEmailIsEmpty() throws Exception {
        user = User.builder()
                .email("")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenEmailWithOutAtSign() throws Exception {
        user = User.builder()
                .email("emailleo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenEmailHasUnacceptableSymbols() throws Exception {
        user = userDbStorage.create(User.builder()
                .email("ema?il@le o.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenLoginIsNull() throws Exception {
        user = User.builder()
                .email("email@leo.ru")
                .login(null)
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenLoginIsNEmpty() throws Exception {
        user = userDbStorage.create(User.builder()
                .email("email@leo.ru")
                .login("")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenLoginHasWhiteSpaces() throws Exception {
        user = userDbStorage.create(User.builder()
                .email("email@leo.ru")
                .login("l og in")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void createPostWhenBirthdayInFuture() throws Exception {
        user = userDbStorage.create(User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().plusDays(1))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenUserFieldsAreCorrect() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = userDbStorage.create(User.builder()
                .id(1)
                .email("updatedemail@leo.ru")
                .login("updatedlogin")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user2.getEmail()))
                .andExpect(jsonPath("$.login").value(user2.getLogin()))
                .andExpect(jsonPath("$.name").value(user2.getName()))
                .andExpect(jsonPath("$.birthday").value(user2.getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void updateUserWhenIdIsNotExist() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = User.builder()
                .id(2)
                .email("updatedemail@leo.ru")
                .login("updatedlogin")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailIsNull() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = User.builder()
                .login("updatedlogin")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailIsEmpty() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = userDbStorage.create(User.builder()
                .id(1)
                .email("")
                .login("updatedlogin")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailWithOutAtSign() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = userDbStorage.create(User.builder()
                .id(1)
                .email("updatedemailleo.ru")
                .login("updatedlogin")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenEmailHasUnacceptableSymbols() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = userDbStorage.create(User.builder()
                .id(1)
                .email("update?demail@leo.ru")
                .login("updatedlogin")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenLoginIsNull() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = User.builder()
                .id(1)
                .email("updatedemail@leo.ru")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenLoginIsNEmpty() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = userDbStorage.create(User.builder()
                .id(1)
                .email("updatedemail@leo.ru")
                .login("")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenLoginHasWhiteSpaces() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = userDbStorage.create(User.builder()
                .id(1)
                .email("updatedemail@leo.ru")
                .login("updat ed log in")
                .name("Updated name")
                .birthday(LocalDate.now().minusDays(2))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void updatePutWhenBirthdayInFuture() throws Exception {
        User user1 = User.builder()
                .email("email@leo.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = userDbStorage.create(User.builder()
                .id(1)
                .email("updatedemail@leo.ru")
                .login("updatedlogin")
                .name("Updated name")
                .birthday(LocalDate.now().plusDays(2))
                .build());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andReturn();

        jsonUser = objectMapper.writeValueAsString(user2);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    public void getUserListRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String jsonUser = objectMapper.writeValueAsString(user);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(jsonUser))
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].login").value(user.getLogin()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].birthday").value(user.getBirthday().toString()))
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
                        .value(userDbStorage.findById(1).getEmail()))
                .andExpect(jsonPath("login")
                        .value(userDbStorage.findById(1).getLogin()))
                .andExpect(jsonPath("name")
                        .value(userDbStorage.findById(1).getName()))
                .andExpect(jsonPath("birthday")
                        .value(userDbStorage.findById(1).getBirthday().toString()))
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
            jsonUser = objectMapper.writeValueAsString(
                    User.builder()
                            .email(i + "email1@leo.ru")
                            .login("login" + i)
                            .name("name" + i)
                            .birthday(LocalDate.parse("1990-12-27").plusYears(i))
                            .build());
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
            jsonUser = objectMapper.writeValueAsString(
                    userDbStorage.create(User.builder()
                            .email(i + "email1@leo.ru")
                            .login("login" + i)
                            .name("name" + i)
                            .birthday(LocalDate.parse("1990-12-27").plusYears(i))
                            .build()));
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
    public void deleteFriendWhenIdsAreExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(
                    userDbStorage.create(User.builder()
                            .email(i + "email1@leo.ru")
                            .login("login" + i)
                            .name("name" + i)
                            .birthday(LocalDate.parse("1990-12-27").plusYears(i))
                            .build()));
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
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()))
                .andReturn();
    }

    @Test
    public void deleteFriendWhenFriendsAreNotExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(
                    userDbStorage.create(User.builder()
                            .email(i + "email1@leo.ru")
                            .login("login" + i)
                            .name("name" + i)
                            .birthday(LocalDate.parse("1990-12-27").plusYears(i))
                            .build()));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdPassingException))
                .andExpect(result -> assertEquals("Не существует  друзей с ID :1, 2",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void deleteFriendWhenIdsAreNotInteger() throws Exception {
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
            jsonUser = objectMapper.writeValueAsString(
                    userDbStorage.create(User.builder()
                            .email(i + "email1@leo.ru")
                            .login("login" + i)
                            .name("name" + i)
                            .birthday(LocalDate.parse("1990-12-27").plusYears(i))
                            .build()));
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
                        .value(userDbStorage.findById(2).getEmail()))
                .andExpect(jsonPath("$[0].login")
                        .value(userDbStorage.findById(2).getLogin()))
                .andExpect(jsonPath("$[0].name")
                        .value(userDbStorage.findById(2).getName()))
                .andExpect(jsonPath("$[0].birthday")
                        .value(userDbStorage.findById(2).getBirthday().toString()))
                .andReturn();
    }

    @Test
    public void getFriendByIdWhenIdsAreExistAndFriendAreNotExist() throws Exception {
        String jsonUser;
        for (int i = 1; i < 3; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            jsonUser = objectMapper.writeValueAsString(
                    userDbStorage.create(User.builder()
                            .email(i + "email1@leo.ru")
                            .login("login" + i)
                            .name("name" + i)
                            .birthday(LocalDate.parse("1990-12-27").plusYears(i))
                            .build()));
            this.mockMvc.perform(MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(jsonUser))
                    .andReturn();
        }

        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/2/friends"))
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
            jsonUser = objectMapper.writeValueAsString(
                    userDbStorage.create(User.builder()
                            .email(i + "email1@leo.ru")
                            .login("login" + i)
                            .name("name" + i)
                            .birthday(LocalDate.parse("1990-12-27").plusYears(i))
                            .build()));
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
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/3/friends/1"))
                .andExpect(status().isOk())
                .andReturn();
        this.mockMvc.perform(MockMvcRequestBuilders.put("/users/3/friends/2"))
                .andExpect(status().isOk())
                .andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email")
                        .value(userDbStorage.findById(2).getEmail()))
                .andExpect(jsonPath("$[0].login")
                        .value(userDbStorage.findById(2).getLogin()))
                .andExpect(jsonPath("$[0].name")
                        .value(userDbStorage.findById(2).getName()))
                .andExpect(jsonPath("$[0].birthday")
                        .value(userDbStorage.findById(2).getBirthday().toString()))
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