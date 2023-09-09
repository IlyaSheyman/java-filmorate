package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidationTest {

    static FilmController controller;

    @BeforeAll()
    public static void createController() {
         controller = new FilmController();
    }

    @Test
    public void shouldHaveNotEmptyName() throws ValidationException {
        Film film = Film.builder()
                .name("")
                .description("Test film")
                .duration(120)
                .releaseDate(LocalDate.parse("1900-03-25"))
                .build();
        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldHaveShortDescription() {
        Film film = Film.builder()
                .name("Name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .duration(120)
                .releaseDate(LocalDate.parse("2000-03-25"))
                .build();

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
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

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }
}
