package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {

    Film addFilm(Film film);

    Film deleteFilm(int id);

    Film updateFilm(Film film);

    HashMap<Integer, Film> getFilms();

    Film getFilm(int id);
}
