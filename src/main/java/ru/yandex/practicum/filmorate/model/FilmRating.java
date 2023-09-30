package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.enums.Rating;

public class FilmRating {

    private int ratingId;
    private String name;

    public FilmRating(int ratingId) {
        this.ratingId = ratingId;
        this.name = getRatingName(ratingId);
    }

    public String getRatingName(int id) {
        switch(id) {
            case(1):
                return Rating.G.getRating();
            case(2):
                return Rating.NC_17.getRating();
            case(3):
                return Rating.R.getRating();
            case(4):
                return Rating.PG.getRating();
            case(5):
                return Rating.PG_13.getRating();
        }
        return null;
    }
}
