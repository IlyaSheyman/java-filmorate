package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UnknownException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/films")
public class FilmController {

    private int seq;
    public HashMap<Integer, Film> films = new HashMap<>();
    public final static LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!validateFilm(film)) {
            log.info("Произошла ошибка валидации фильма");
            throw new ValidationException("Ошибка валидации фильма");
        } else {
            seq++;
            film.setId(seq);
            films.put(film.getId(), film);
            log.info("Фильм с id {} и именем {} успешно добавлен", film.getId(), film.getName());
            return film;
        }
    }

    private boolean validateFilm(@Valid Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            return false;
        } else {
            return true;
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, UnknownException {
        if (films.containsKey(film.getId())) {
            Film previous = films.remove(film.getId());
            previous.setId(film.getId());
            previous.setName(film.getName());
            previous.setDescription(film.getDescription());
            previous.setReleaseDate(film.getReleaseDate());
            previous.setDuration(film.getDuration());
            films.put(previous.getId(), previous);

            log.info("Фильм с id {} и именем {} успешно обновлён", film.getId(), film.getName());
        } else {
            throw new UnknownException("Такой фильм не добавлен"); // хотя здесь можно было бы реализовать добавление фильма..
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        if (!films.isEmpty()) {
            List<Film> filmList = new ArrayList<>(films.values());
            return filmList;
        } else {
            log.info("Список фильмов пуст");
            return new ArrayList<>();
        }
    }
}
