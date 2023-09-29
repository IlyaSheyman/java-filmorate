package ru.yandex.practicum.filmorate.model.enums;

public enum Genre {

    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    TRILLER("Триллер"),
    DOCUMENTAL("Документальный"),
    SHOOTER("Боевик");

    private String genre;

    Genre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }
}



