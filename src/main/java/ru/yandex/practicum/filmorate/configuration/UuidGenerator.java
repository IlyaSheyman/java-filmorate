package ru.yandex.practicum.filmorate.configuration;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidGenerator {

    public String nextUuid() {
        return UUID.randomUUID().toString();
    }
}
