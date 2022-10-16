package ru.practicum.shareit.booking.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingForBooker(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @PathParam("state") String state,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBookingByUserId(userId, state, from, size);
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @PathVariable Integer bookingId, @PathParam("approved") @NonNull Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @PathParam("state") String state,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBookingByOwnerId(userId, state, from, size);
    }

}
