package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDtoWithBooking getItemById(Integer userId, Integer id);

    List<ItemDtoWithBooking> getAllItems(Integer userId);

    ItemDto create(Integer id, ItemDto itemDto);

    ItemDto update(Integer userId, ItemDto itemDto, Integer itemId);

    List<ItemDto> searchItemByQuery(String query);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);

}
