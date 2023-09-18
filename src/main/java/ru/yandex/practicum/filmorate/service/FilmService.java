package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film likeFilm(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        } else if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        } else {
            filmStorage.getFilms().get(filmId).getLikes().add(userId);

            return filmStorage.getFilms().get(filmId);
        }
    }

    public Film deleteLike(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        } else if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        } else {
            filmStorage.getFilms().get(filmId).getLikes().remove(userId);
            return filmStorage.getFilms().get(filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getFilms().values());

        List<Film> sortedFilms = films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .collect(Collectors.toList());

        if (count < films.size()) {
            return sortedFilms.stream()
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return sortedFilms;
        }
    }
//        return films.stream()
//                .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
//                .limit(count)
//                .collect(Collectors.toList())
}
