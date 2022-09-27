package ru.practicum.shareit.request;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequest {
    Integer id;
    String description;
    User requester;
    LocalDateTime created;

}
