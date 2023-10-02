package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDBStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addToFriendList(int id, int friendId) {
        if (userStorage.getUsers().containsKey(id) && userStorage.getUsers().containsKey(friendId)) {
            userStorage.createFriendship(id, friendId);
            return userStorage.getUser(id);
        } else {
            throw new NotFoundException("Один из пользователей не найден");
        }
    }

    public User deleteFromFriendList(int id, int friendId) {
        if (userStorage.getUsers().containsKey(id) && userStorage.getUsers().containsKey(friendId)) {
            return userStorage.deleteFriendship(id, friendId);
        } else {
            throw new NotFoundException("Один из пользователей не найден");
        }
    }

    public List<User> getFriendList(int id) {
        if (userStorage.getUsers().containsKey(id)) {
            return userStorage.getFriends(id);
        } else {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    public List<User> getCommonFriends(int id, int friendsId) {
        if (userStorage.getUsers().containsKey(id) && userStorage.getUsers().containsKey(friendsId)) {

            List<User> friends = userStorage.getFriends(id);
            List<User> otherFriends = userStorage.getFriends(friendsId);

            return friends.stream()
                    .filter(otherFriends::contains)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Один из пользователей не найден");
        }
    }
}
