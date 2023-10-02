package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Rating;

@Data
public class MPA {

    private int id;
    private transient String name;

    public MPA(int id) {
        this.id = id;
        this.name = getRatingName(id);
    }

    public MPA() {
    }

    public int getId() {
        return id;
    }

    public String getRatingName(int id) {
        switch(id) {
            case(1):
                return Rating.G.getRating();
            case(2):
                return Rating.PG.getRating();
            case(3):
                return Rating.PG_13.getRating();
            case(4):
                return Rating.R.getRating();
            case(5):
                return Rating.NC_17.getRating();
        }
        return null;
    }
}
