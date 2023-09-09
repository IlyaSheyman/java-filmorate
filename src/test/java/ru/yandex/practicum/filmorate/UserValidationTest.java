package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTest {

    static UserController controller;

    @BeforeAll()
    public static void createController() {
        controller = new UserController();
    }

    @Test
    public void shouldHaveEmail() {
        User user = User.builder()
                .email("ilya")
                .name("Ilya")
                .login("Sheyman")
                .birthday(LocalDate.parse("2004-09-05"))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void shouldHaveLogin() {
        User user = User.builder()
                .email("ilya@yandex.ru")
                .name("Ilya")
                .login(" ")
                .birthday(LocalDate.parse("2004-09-05"))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void shouldHaveName() throws ValidationException {
        User user = User.builder()
                .email("ilya@yandex.ru")
                .name("")
                .login("Sheyman")
                .birthday(LocalDate.parse("2004-09-05"))
                .build();

        User newUser = controller.createUser(user);
        assertEquals("Sheyman", newUser.getName());
    }

    @Test
    public void shouldHaveCorrectBirthday() {
        User user = User.builder()
                .email("ilya@yandex.ru")
                .name("Ilya")
                .login("Sheyman")
                .birthday(LocalDate.parse("2040-09-05"))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }
}
