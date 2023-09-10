package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.exceptions.UnknownException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/users")
public class UserController {
    private int seq;
    public HashMap<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        if (validateUser(user) == false) {
            log.info("Ошибка валидации пользователя {}", user.getName());
            throw new ValidationException("Ошибка валидации пользователя");
        } else {
            seq++;
            user.setId(seq);
            users.put(user.getId(), user);
            log.info("Пользователь с id {} и именем {} успешно создан!", user.getId(), user.getName());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, UnknownException {
        if (users.containsKey(user.getId())) {
            User previous = users.remove(user.getId());
            previous.setId(user.getId());
            previous.setName(user.getName());
            previous.setBirthday(user.getBirthday());
            previous.setLogin(user.getLogin());
            previous.setEmail(user.getEmail());
            users.put(previous.getId(), previous);

            log.info("Пользователь с id {} и именем {} успешно обновлен!", previous.getId(), previous.getName());
        } else {
            throw new UnknownException("Такой пользователь не добавлен");
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        if (!users.isEmpty()) {
            List<User> userList = new ArrayList<>(users.values());
            return userList;
        } else {
            log.info("Список пользователей пуст");
            return new ArrayList<>();
        }
    }

    private boolean validateUser(@Valid User user) {
        if (user.getLogin().contains(" ")) {
            return false;
        } else if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            return true;
        }
        return true;
    }
}
