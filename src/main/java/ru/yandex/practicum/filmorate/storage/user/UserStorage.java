package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    HashMap<Integer, User> getUsers();

    User getUser(int id);

    List<User> getFriends(int id);

    Friendship createFriendship(int id, int friendId);

    User deleteFriendship(int id, int friendId);
}
