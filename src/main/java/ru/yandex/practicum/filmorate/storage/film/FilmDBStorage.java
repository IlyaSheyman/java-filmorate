package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Repository("filmDBStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDBStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    public final static LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");
    private HashMap<Integer, Film> films = new HashMap<>();
    private HashMap<Integer, String> genreNames = new HashMap<>();

    @Override
    public Film addFilm(Film film) throws ValidationException {
        if (!validateFilm(film)) {
            log.info("Произошла ошибка валидации фильма");
            throw new ValidationException("Ошибка валидации фильма");
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?);";
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatementCreatorFactory pscf =
                            new PreparedStatementCreatorFactory(sql,
                                    Types.VARCHAR,
                                    Types.VARCHAR,
                                    Types.TIMESTAMP,
                                    Types.INTEGER,
                                    Types.INTEGER);
                    pscf.setReturnGeneratedKeys(true);

                    PreparedStatementCreator psc =
                            pscf.newPreparedStatementCreator(
                                    Arrays.asList(
                                            film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId())
                            );
                    return psc.createPreparedStatement(con);
                }
            }, keyHolder);

            film.setId(keyHolder.getKey().intValue());

            log.info("Фильм с id {} и именем {} успешно добавлен", film.getId(), film.getName());
            return film;
        }
    }

    @Override
    public Film deleteFilm(int id) {
        String sql = "DELETE FROM films WHERE id = ?;";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        if (rowsDeleted == 0) {
            throw new NotFoundException("Фильм с идентификатором " + id + " не найден");
        }

        return getFilms().get(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilms().containsKey(film.getId())) {
            String sql = "UPDATE films SET " +
                    "name = ?, " +
                    "description = ?, " +
                    "release_date = ?, " +
                    "duration = ?, " +
                    "rating_id = ? " +
                    "WHERE id = ?;";

            jdbcTemplate.update(sql, film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            log.info("Фильм с id {} и именем {} успешно обновлён", film.getId(), film.getName());
        } else {
            throw new NotFoundException("Такой фильм не добавлен");
        }
        return film;
    }

    @Override
    public HashMap<Integer, Film> getFilms() {
        String sql = "SELECT * FROM films";
        List<Film> filmsList = jdbcTemplate.query(sql, new FilmRowMapper());

        for (Film film : filmsList) {
            films.put(film.getId(), film);
        }

        return films;
    }

    @Override
    public Film getFilm(int id) {
        if (getFilms().containsKey(id)) {
            return getFilms().get(id);
        } else {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
    }

    private boolean validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Like addLike(int userId, int filmId) {
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            return ps;
        }, keyHolder);

        // Извлекаем сгенерированный ключ
        int generatedId = keyHolder.getKey().intValue();

        // Создаем объект Friendship с сгенерированным ключом
        Like like = Like.builder()
                .id(generatedId)
                .userId(userId)
                .filmId(filmId)
                .build();

        return like;
    }

    @Override
    public Film deleteLike(int userId, int filmId) {
        String sql = "DELETE FROM likes WHERE (user_id = ? AND film_id = ?);";
        int rowsDeleted = jdbcTemplate.update(sql, userId, filmId);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Like от пользователя id " + userId + " фильму " + filmId + " не поставлен");
        }
        return getFilms().get(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (getLikes() == 0) {
            return new ArrayList<>(getFilms().values());
        } else {
            String sql = "SELECT films.id, films.name, films.description, films.release_date, films.duration, rating_id, COUNT(likes.id) AS like_count " +
                    "FROM films " +
                    "LEFT JOIN likes ON films.id = likes.film_id " +
                    "GROUP BY films.id " +
                    "ORDER BY like_count DESC " +
                    "LIMIT ?;";

            List<Film> filmsList = jdbcTemplate.query(sql, new Object[]{count}, (rs, rowNum) -> {
                return Film.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .mpa(new MPA(rs.getInt("rating_id")))
                        .build();
            });
            return filmsList;
        }
    }

    private Integer getLikes() {
        Integer likes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes;", Integer.class);
        return likes;
    }

    @Override
    public List<FilmGenre> getGenres() {
        List<FilmGenre> genres = jdbcTemplate.query("SELECT * FROM genre;", new RowMapper<FilmGenre>() {
                    @Override
                    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new FilmGenre(rs.getInt("id"));
                    }
                });
        return genres;
    }

    @Override
    public FilmGenre getFilmGenre(int id) {
        if (id > 10) {
            throw new NotFoundException("Жанра с id " + id + " не существует");
        }
        FilmGenre filmGenre = jdbcTemplate.queryForObject("SELECT * FROM genre WHERE id = ?;", new RowMapper<FilmGenre>() {
            @Override
            public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new FilmGenre(rs.getInt("id"));
            }
        }, id);
        return filmGenre;
    }

    @Override
    public List<MPA> getFilmRatings() {
        List<MPA> ratings = jdbcTemplate.query("SELECT * FROM rating;", new RowMapper<MPA>() {
            @Override
            public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new MPA(rs.getInt("id"));
            }
        });
        return ratings;
    }

    @Override
    public MPA getFilmRating(int id) {
        if (id > 10) {
            throw new NotFoundException("MPA с таким id не существует");
        }
        MPA mpa = jdbcTemplate.queryForObject("SELECT * FROM rating WHERE id = ?;", new RowMapper<MPA>() {
            @Override
            public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new MPA(rs.getInt("id"));
            }
        }, id);
        return mpa;
    }

    private class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(new MPA(rs.getInt("rating_id")))
                    .build();
        }
    }
}