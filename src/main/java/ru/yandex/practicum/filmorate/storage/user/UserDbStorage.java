package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component("userDBStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private HashMap<Integer, User> users = new HashMap<>();

    @Autowired
    public UserDbStorage() {
        this.jdbcTemplate = new JdbcTemplate();
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (validateUser(user) == false) {
            log.info("Ошибка валидации пользователя {}", user.getName());
            throw new ValidationException("Ошибка валидации пользователя");
        } else {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setTimestamp(4, Timestamp.valueOf(user.getBirthday().atStartOfDay()));

                return ps;
            }, keyHolder);

            user.setId(keyHolder.getKey().intValue());

            log.info("Пользователь с id {} и именем {} успешно создан!", user.getId(), user.getName());

            return user;
        }
    }

    @Override
    public User updateUser(@RequestBody User user) {
        if (getUsers().containsKey(user.getId())) {

            String sql = "UPDATE users " +
                    "SET name = ?, " +
                    "    birthday = ?, " +
                    "    login = ?, " +
                    "    email = ? " +
                    "WHERE id = ?";

            jdbcTemplate.update(sql, user.getName(), user.getBirthday(), user.getLogin(), user.getEmail(), user.getId());
            log.info("Пользователь с id {} и именем {} успешно обновлен!", user.getId(), user.getName());
        } else {
            throw new NotFoundException("Такой пользователь не добавлен");
        }
        return user;
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден");
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

    @Override
    public HashMap<Integer, User> getUsers() {
        String sql = "SELECT * FROM users";

        List<User> userList = jdbcTemplate.query(sql, new UserRowMapper());

        for (User user : userList) {
            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public User getUser(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new UserRowMapper(), id);
    }

    public List<Friendship> getFriendships() {
        List<Friendship> friendships = jdbcTemplate.query("SELECT * FROM friendships",
                new RowMapper<Friendship>() {
                    @Override
                    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Friendship.builder()
                                .id(rs.getInt("friendship_id"))
                                .userId(rs.getInt("user_id"))
                                .friendship(rs.getBoolean("friendship_status"))
                                .friendId(rs.getInt("friend_id"))
                                .build();
                    }
                });
        return friendships;
    }

    public List<User> getFriends(int id) {
        List<Friendship> friendships = jdbcTemplate.query("SELECT * FROM friendships WHERE user_id = ?",
                new RowMapper<Friendship>() {
                    @Override
                    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Friendship.builder()
                                .id(rs.getInt("friendship_id"))
                                .friendship(rs.getBoolean("friendship_status"))
                                .friendId(rs.getInt("friend_id"))
                                .build();
                    }
                }, id);

        List<User> friends = new ArrayList<>();

        for (Friendship f : friendships) {
            friends.add(users.get(f.getFriendId()));
        }

        return friends;
    }

    @Override
    public Friendship createFriendship(int id, int friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id, friendship_status) VALUES (?, ?, ?)";

        // Создаем объект KeyHolder для получения сгенерированного ключа
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Выполняем вставку и передаем KeyHolder
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, id);
            ps.setInt(2, friendId);
            ps.setBoolean(3, getFriendshipBoolean(id, friendId)); // Например, friendship_status = true при создании дружбы
            return ps;
        }, keyHolder);

        // Извлекаем сгенерированный ключ
        int generatedId = keyHolder.getKey().intValue();

        // Создаем объект Friendship с сгенерированным ключом
        Friendship friendship = Friendship
                .builder().
                id(generatedId)
                .userId(id)
                .friendId(friendId)
                .friendship(getFriendshipBoolean(id, friendId))
                .build();

        return friendship;
    }


    private Boolean getFriendshipBoolean(int id, int friendId) {
        int seq = 0;
        for (Friendship frship : getFriendships()) {
            if (frship.getFriendId() == friendId && frship.getUserId() == id ||
                    frship.getFriendId() == id && frship.getUserId() == friendId) {
                seq++;
            }
        }
        if (seq==1) {
            return false;
        } else if (seq==2) {
            return true;
        }
        return null;
    }

    @Override
    public User deleteFriendship(int id, int friendId) {
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?)";

        int rowsDeleted = jdbcTemplate.update(sql, id, friendId);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Friendship между пользователями с id " + id + " и " + friendId + " не найдено");
        }
        return getUser(id);
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getInt("id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(LocalDate.parse(rs.getString("birthday")))
                    .build();
        }
    }
}


