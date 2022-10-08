package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping(value = "/{id}")
    public ItemDtoWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer id) {
        return itemService.getItemById(userId, id);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemService.getAllItems(ownerId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId,itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody ItemDto itemDto, @PathVariable Integer id) {
        return itemService.update(userId,itemDto,id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByQuery(@RequestParam(name = "text") String query) {
        return itemService.searchItemByQuery(query);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId, @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

}
