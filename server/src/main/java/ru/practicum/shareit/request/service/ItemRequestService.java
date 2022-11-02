package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllRequestsForUser(Integer userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Integer userId, Integer id);

    List<ItemRequestDto> getAllItemRequest(Integer userId);
}
