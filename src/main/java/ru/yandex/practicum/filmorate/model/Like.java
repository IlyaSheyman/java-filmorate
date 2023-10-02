package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

@Builder
public class Like {
    private int id;
    private int filmId;
    private int userId;

    public Like(int id, int filmId, int userId) {
        this.id = id;
        this.filmId = filmId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}


