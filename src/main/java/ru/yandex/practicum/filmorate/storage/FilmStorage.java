package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {

    Film addFilm(Film film) throws ValidationException;

    Film deleteFilm(int id);

    Film updateFilm(Film film) throws ValidationException, NotFoundException;

    HashMap<Integer, Film> getFilms();

    Film getFilm(int id);
}
