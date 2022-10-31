package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody ItemDto itemDto) {

        if (itemDto.getDescription() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getName().isBlank()) {
            throw new ValidationException("Название предмета не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступа не может быть пустым");
        }

        log.info("Create Item={}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody ItemDto itemDto, @PathVariable Integer id) {
        log.info("Update Item with ID = {}", id);
        return itemClient.updateItem(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable Integer id) {
        log.info("Get Item with ID = {}", id);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> retrieveAllItem(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Get all items for user with ID = {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByKeyword(@RequestParam(name = "text", defaultValue = "") String keyword,
                                                      @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Search item by keyword={}", keyword);
        return itemClient.searchItemByKeyword(keyword, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer itemId, @RequestBody CommentDto commentDto) {
        log.info("Add comment for Item's ID = {} by User with ID = {}", itemId, userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}