package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/genres")
public class GenreController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public GenreController(@Qualifier("filmDBStorage") FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<FilmGenre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("{id}") // приходит идентификатор фильма
    public FilmGenre getGenre(@PathVariable int id) {
        return filmService.getFilmGenre(id);
    }
}
