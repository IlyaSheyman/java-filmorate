//package ru.yandex.practicum.filmorate.storage.user;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestBody;
//import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
//import ru.yandex.practicum.filmorate.exceptions.ValidationException;
//import ru.yandex.practicum.filmorate.model.User;
//
//import javax.validation.Valid;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//
//@Data
//@Slf4j
//@Component("inMemoryUserStorage")
//public class InMemoryUserStorage implements UserStorage {
//    private int seq;
//    public HashMap<Integer, User> users = new HashMap<>();
//
//    @Override
//    public User createUser(User user) {
//        if (validateUser(user) == false) {
//            log.info("Ошибка валидации пользователя {}", user.getName());
//            throw new ValidationException("Ошибка валидации пользователя");
//        } else {
//            Set<Integer> friends = new HashSet<>();
//            seq++;
//            user.setId(seq);
//            user.setFriends(friends);
//            users.put(user.getId(), user);
//            log.info("Пользователь с id {} и именем {} успешно создан!", user.getId(), user.getName());
//        }
//        return user;
//    }
//
//    @Override
//    public User updateUser(@RequestBody User user) {
//        if (users.containsKey(user.getId())) {
//            User previous = users.remove(user.getId());
//            previous.setId(user.getId());
//            previous.setName(user.getName());
//            previous.setBirthday(user.getBirthday());
//            previous.setLogin(user.getLogin());
//            previous.setEmail(user.getEmail());
//            users.put(previous.getId(), previous);
//
//            log.info("Пользователь с id {} и именем {} успешно обновлен!", previous.getId(), previous.getName());
//        } else {
//            throw new NotFoundException("Такой пользователь не добавлен");
//        }
//        return user;
//    }
//
//    @Override
//    public void deleteUser(int id) {
//        if (users.containsKey(id)) {
//            users.remove(id);
//        } else {
//            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден");
//        }
//    }
//
//    private boolean validateUser(@Valid User user) {
//        if (user.getLogin().contains(" ")) {
//            return false;
//        } else if (user.getName() == null || user.getName().isEmpty()) {
//            user.setName(user.getLogin());
//            return true;
//        }
//        return true;
//    }
//
//    @Override
//    public HashMap<Integer, User> getUsers() {
//        return users;
//    }
//
//    @Override
//    public User getUser(int id) {
//        if (users.containsKey(id)) {
//            return users.get(id);
//        } else {
//            throw new NotFoundException("Пользователь с id " + id + " не найден");
//        }
//    }
//
//}
