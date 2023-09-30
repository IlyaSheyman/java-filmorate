package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.enums.Genre;

@Getter
@Setter
public class FilmGenre {
    private int filmId;
    private int genreId;
    private String name;

    public FilmGenre(int filmId, int genreId) {
        this.filmId = filmId;
        this.genreId = genreId;
        this.name = getGenreName(genreId);
    }

    public String getGenreName(int id) {
        switch (id) {
            case(1):
                return Genre.COMEDY.getGenre();
            case(2):
                return Genre.DRAMA.getGenre();
            case(3):
                return Genre.SHOOTER.getGenre();
            case(4):
                return Genre.TRILLER.getGenre();
            case(5):
                return Genre.CARTOON.getGenre();
        }
        return null;
    }

}
