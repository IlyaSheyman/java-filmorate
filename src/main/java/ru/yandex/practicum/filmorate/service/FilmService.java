package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.List;

@Service
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("userDBStorage") UserStorage userStorage,
                       @Qualifier("filmDBStorage") FilmStorage filmStorage)
    {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Like likeFilm(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        } else if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        } else {
            return filmStorage.addLike(userId, filmId);
        }
    }

    public Film deleteLike(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        } else if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        } else {
            return filmStorage.deleteLike(userId, filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public HashMap<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    public List<FilmGenre> getGenres() {
        return filmStorage.getGenres();
    }

    public FilmGenre getFilmGenre(int id) {
        return filmStorage.getFilmGenre(id);
    }

    public List<MPA> getFilmRatings() {
        return filmStorage.getFilmRatings();
    }

    public MPA getFilmRating(int id) {
        return filmStorage.getFilmRating(id);
    }
}
