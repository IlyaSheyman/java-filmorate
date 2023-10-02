package ru.yandex.practicum.filmorate.model;

import lombok.Builder;

@Builder
public class Friendship {

    private int id;
    private int userId;
    private int friendId;
    private boolean friendship;

    public Friendship(int id, int userId, int friendId, boolean friendship) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.friendship = friendship;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public boolean isFriendship() {
        return friendship;
    }

    public void setFriendship(boolean friendship) {
        this.friendship = friendship;
    }
}
