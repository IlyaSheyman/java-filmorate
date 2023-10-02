package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/mpa")
public class RatingController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public RatingController(@Qualifier("filmDBStorage") FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<MPA> getFilmRatings() {
        return filmService.getFilmRatings();
    }

    @GetMapping("{id}") // приходит идентификатор фильма
    public MPA getFilmRating(@PathVariable int id) {
        return filmService.getFilmRating(id);
    }
}
