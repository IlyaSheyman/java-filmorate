package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

public class FilmValidationTest {

    static FilmController controller;

//    @BeforeAll()
//    public static void createController() {
//         controller = new FilmController();
//    }

    @Test
    public void shouldHaveNotEmptyName() throws ValidationException {
        Film film = Film.builder()
                .name("")
                .description("Test film")
                .duration(120)
                .releaseDate(LocalDate.parse("1900-03-25"))
                .build();

        controller.addFilm(film);
        List<Film> filmList = controller.getFilms();
        assertEquals(0, filmList.size());
    }

    @Test
    public void shouldHaveShortDescription() {
        Film film = Film.builder()
                .name("Name")
                .description("N".repeat(201))
                .duration(120)
                .releaseDate(LocalDate.parse("2000-03-25"))
                .build();

        controller.addFilm(film);
        List<Film> filmList = controller.getFilms();
        assertEquals(0, filmList.size());
    }

    @Test
    public void shouldBeAfterMinReleaseDate() {
        Film film = Film.builder()
                .name("Name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.parse("1890-03-25"))
                .build();

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldHaveDuration() {
        Film film = Film.builder()
                .name("Name")
                .description("description")
                .duration(-120)
                .releaseDate(LocalDate.parse("2000-03-25"))
                .build();

        controller.addFilm(film);
        List<Film> filmList = controller.getFilms();
        assertEquals(0, filmList.size());
    }
}
