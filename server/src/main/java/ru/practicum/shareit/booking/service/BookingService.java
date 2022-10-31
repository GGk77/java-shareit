package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingDto bookingDto, Integer userId);

    BookingDto update(Integer userId, Integer bookingId, Boolean isApproved);

    BookingDto getBookingById(Integer bookingId, Integer userId);

    List<BookingDto> getAllBookingByOwnerId(Integer ownerId, State state, Integer from, Integer size);

    List<BookingDto> getAllBookingByUserId(Integer userId, State state, Integer from, Integer size);
}
