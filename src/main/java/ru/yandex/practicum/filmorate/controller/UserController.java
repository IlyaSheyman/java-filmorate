package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userDBStorage") UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        User created = userStorage.createUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        User updated = userStorage.updateUser(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userStorage.deleteUser(id);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userStorage.getUser(id);
    }

    @GetMapping()
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(userStorage.getUsers().values());
        return userList;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriendsList(@PathVariable int id, @PathVariable int friendId) {
        return userService.addToFriendList(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFromFriendList(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFromFriendList(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> printFriends(@PathVariable int id) {
        return userService.getFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int friendId) {
        return userService.getCommonFriends(id, friendId);
    }
}
