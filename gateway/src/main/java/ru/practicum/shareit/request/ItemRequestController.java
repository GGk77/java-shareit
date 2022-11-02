package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {

        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Description has to be not empty");
        }

        log.info("Create ItemRequest={}", itemRequestDto);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllMyItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Get all own requests for user={}", userId);
        return itemRequestClient.getAllOwnItemRequests(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        log.info("Get request with id={}", id);
        return itemRequestClient.getItemRequest(userId, id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsForUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @PositiveOrZero @RequestParam(required = false, defaultValue = "0")
                                                        Integer from,
                                                        @Positive @RequestParam(required = false, defaultValue = "10")
                                                        Integer size) {
        log.info("Get all user's requests for user={}", userId);
        return itemRequestClient.getAllUserRequest(userId, from, size);
    }
}