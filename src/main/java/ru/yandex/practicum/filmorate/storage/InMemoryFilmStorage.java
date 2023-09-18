package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int seq;
    public final static LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");
    public HashMap<Integer, Film> films = new HashMap<>();


    @Override
    public Film addFilm(Film film) throws ValidationException {
        if (!validateFilm(film)) {
            log.info("Произошла ошибка валидации фильма");
            throw new ValidationException("Ошибка валидации фильма");
        } else {
            Set<Integer> likes = new HashSet<>();
            seq++;
            film.setId(seq);
            film.setLikes(likes);
            films.put(film.getId(), film);
            log.info("Фильм с id {} и именем {} успешно добавлен", film.getId(), film.getName());
            return film;
        }
    }

    @Override
    public Film deleteFilm(int id) {
        if (films.containsKey(id)) {
            return films.remove(id);
        } else {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
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
            throw new NotFoundException("Такой фильм не добавлен");
        }
        return film;
    }

    private boolean validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public HashMap<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilm(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
    }
}
