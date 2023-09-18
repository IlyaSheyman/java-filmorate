package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage {
    User createUser(User user) throws ValidationException;

    User updateUser(User user) throws ValidationException, NotFoundException;

    void deleteUser(int id);

    HashMap<Integer, User> getUsers();

    User getUser(int id);
}
