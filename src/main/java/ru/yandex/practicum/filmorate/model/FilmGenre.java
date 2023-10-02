package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.enums.Genre;

@Getter
@Setter
public class FilmGenre {

    private int genreId;
    private transient String name;

    public FilmGenre(int genreId) {
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
                return Genre.CARTOON.getGenre();
            case(4):
                return Genre.TRILLER.getGenre();
            case(5):
                return Genre.DOCUMENTAL.getGenre();
            case(6):
                return Genre.SHOOTER.getGenre();
        }
        return null;
    }

}
