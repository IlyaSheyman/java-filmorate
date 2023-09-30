package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashMap;
import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film deleteFilm(int id);

    Film updateFilm(Film film);

    HashMap<Integer, Film> getFilms();

    Film getFilm(int id);

    Like addLike(int userId, int filmId);

    Film deleteLike(int userId, int filmId);

    List<Film> getPopularFilms(int count);

    HashMap<Integer, String> getGenres();

    FilmGenre getFilmGenre(int id);

    List<FilmRating> getFilmRatings();

    FilmRating getFilmRating(int id);
}
