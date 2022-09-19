package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(value = "/{id}")
    public ItemDto getItemById(@PathVariable Integer id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public Set<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllItems(userId);
    }

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId,itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Validated({Update.class}) @RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody ItemDto itemDto, @PathVariable Integer id) {
        return itemService.update(userId,itemDto,id);
    }

    @GetMapping("/search")
    public Set<ItemDto> searchItemByQuery(@RequestParam(name = "text") String query) {
        return itemService.searchItemByQuery(query)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

}
