package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository("userDBStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (validateUser(user) == false) {
            log.info("Ошибка валидации пользователя {}", user.getName());
            throw new ValidationException("Ошибка валидации пользователя");
        } else {
            if (validateUser(user)) {
                // Создаем экземпляр SimpleJdbcInsert
                SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("users")
                        .usingGeneratedKeyColumns("id");

                // Создаем параметры для вставки
                MapSqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue("email", user.getEmail())
                        .addValue("login", user.getLogin())
                        .addValue("name", user.getName())
                        .addValue("birthday", Timestamp.valueOf(user.getBirthday().atStartOfDay()));

                Number id = simpleJdbcInsert.executeAndReturnKey(parameters);

                user.setId(id.intValue());

                log.info("Пользователь с id {} и именем {} успешно создан!", user.getId(), user.getName());

                return user;
            } else {
                log.info("Ошибка валидации пользователя {}", user.getName());
                throw new ValidationException("Ошибка валидации пользователя");
            }
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
                    "WHERE id = ?;";

            jdbcTemplate.update(sql, user.getName(), user.getBirthday(), user.getLogin(), user.getEmail(), user.getId());
            log.info("Пользователь с id {} и именем {} успешно обновлен!", user.getId(), user.getName());
        } else {
            throw new NotFoundException("Такой пользователь не добавлен");
        }
        return user;
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?;";
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
        String sql = "SELECT * FROM users;";

        List<User> userList = jdbcTemplate.query(sql, new UserRowMapper());

        for (User user : userList) {
            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public User getUser(int id) {
        if (getUsers().containsKey(id)) {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?;", new UserRowMapper(), id);
        } else {
            throw new NotFoundException("Пользователь с id {} не найден");
        }
    }

    public List<Friendship> getFriendships() {
        List<Friendship> friendships = jdbcTemplate.query("SELECT * FROM friendships;",
                new RowMapper<Friendship>() {
                    @Override
                    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Friendship.builder()
                                .id(rs.getInt("friendship_id"))
                                .userId(rs.getInt("user_id"))
                                .friendId(rs.getInt("friend_id"))
                                .friendship(rs.getBoolean("friendship_status"))
                                .build();
                    }
                });
        return friendships;
    }

    public List<User> getFriends(int id) {
        List<Friendship> friendships = jdbcTemplate.query("SELECT * FROM friendships WHERE user_id = ?;",
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
//        // Создаем экземпляр SimpleJdbcInsert
//        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("users")
//                .usingGeneratedKeyColumns("id");
//
//        // Создаем параметры для вставки
//        MapSqlParameterSource parameters = new MapSqlParameterSource()
//                .addValue("email", user.getEmail())
//                .addValue("login", user.getLogin())
//                .addValue("name", user.getName())
//                .addValue("birthday", Timestamp.valueOf(user.getBirthday().atStartOfDay()));
//
//        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
//
//        user.setId(id.intValue());
//
//        log.info("Пользователь с id {} и именем {} успешно создан!", user.getId(), user.getName());
//
//        return user;
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendships")
                .usingGeneratedKeyColumns("friendship_id");

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", id)
                .addValue("friend_id", friendId);

        Number friendship_id = simpleJdbcInsert.executeAndReturnKey(parameters);

        log.info("Дружба с id {} между {} и {} успешно создана!", friendship_id, id, friendId);

        Friendship friendship = Friendship
                .builder().
                id(friendship_id.intValue())
                .userId(id)
                .friendId(friendId)
                .friendship(getFriendshipBoolean(id, friendId))
                .build();

        return friendship;
    }


    private Boolean getFriendshipBoolean(int id, int friendId) {
        int seq = 0;

        if (getFriendships().size() == 0) {
            return false;
        } else {
            for (Friendship frship : getFriendships()) {
                if (frship.getFriendId() == friendId && frship.getUserId() == id ||
                        frship.getFriendId() == id && frship.getUserId() == friendId) {
                    seq++;
                }
            }
            if (seq == 1) {
                return false;
            } else if (seq == 2) {
                return true;
            }
            return null;
        }
    }

    @Override
    public User deleteFriendship(int id, int friendId) {
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?);";

        int rowsDeleted = jdbcTemplate.update(sql, id, friendId);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Friendship между пользователями с id " + id + " и " + friendId + " не найдено");
        }
        return getUser(id);
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            int userId = rs.getInt("id");
            String email = rs.getString("email");
            String login = rs.getString("login");
            String name = rs.getString("name");

            // Assuming the birthday is a LocalDateTime in the database
            String birthdayString = rs.getString("birthday");
            LocalDateTime birthday = LocalDateTime.parse(birthdayString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            return User.builder()
                    .id(userId)
                    .email(email)
                    .login(login)
                    .name(name)
                    .birthday(birthday.toLocalDate())  // Convert LocalDateTime to LocalDate
                    .build();
        }
    }
}


