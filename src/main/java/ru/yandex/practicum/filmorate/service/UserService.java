package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addToFriendList(int id, int friendId) {
        if (userStorage.getUsers().containsKey(id) && userStorage.getUsers().containsKey(friendId)) {
            userStorage.getUsers().get(id).getFriends().add(friendId);
            userStorage.getUsers().get(friendId).getFriends().add(id);

            return userStorage.getUsers().get(id);
        } else {
            throw new NotFoundException("Один из пользователей не найден");
        }
    }

    public User deleteFromFriendList(int id, int friendId) {
        if (userStorage.getUsers().containsKey(id) && userStorage.getUsers().containsKey(friendId)) {
            userStorage.getUsers().get(id).getFriends().remove(friendId);
            userStorage.getUsers().get(friendId).getFriends().remove(id);
            return userStorage.getUser(id);
        } else {
            throw new NotFoundException("Один из пользователей не найден");
        }
    }

    public List<User> getFriendList(int id) {
        if (userStorage.getUsers().containsKey(id)) {
            List<User> friends = new ArrayList<>();
            Set<Integer> friendsId = userStorage.getUsers().get(id).getFriends();

            for (Integer current : friendsId) {
                friends.add(userStorage.getUsers().get(current));
            }

            return friends;
        } else {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    public List<User> getCommonFriends(int id, int friendsId) {
        if (userStorage.getUsers().containsKey(id) && userStorage.getUsers().containsKey(friendsId)) {
            List<User> commonFriends = new ArrayList<>();
            Set<Integer> friends = userStorage.getUsers().get(id).getFriends();

            if (friends != null) {
                for (Integer current : friends) {
                    if (userStorage.getUsers().get(friendsId).getFriends().contains(current)) {
                        commonFriends.add(userStorage.getUsers().get(current));
                    }
                }
            }
            return commonFriends;
        } else {
            throw new NotFoundException("Один из пользователей не найден");
        }
    }
}
