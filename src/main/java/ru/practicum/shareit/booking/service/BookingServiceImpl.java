package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public BookingDto create(BookingDto bookingDto, Integer userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d is not found", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d is not found", bookingDto.getItemId())));
        Booking booking = BookingMapper.toBooking(booker, item, bookingDto);
        booking.setStatus(Status.WAITING);
        if (booking.getBooker().getId().equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("owner cant booked this item");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Item is not available");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("cant start after end");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("cant start in the past");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("cant end in the past");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Integer userId, Integer bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("this booking with id %d is not found",
                        bookingId)));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("the user is not the owner");
        }
        if (booking.getStatus().equals(Status.APPROVED) && isApproved) {
            throw new ValidationException("approved");
        }
        if (booking.getStatus().equals(Status.REJECTED) && !isApproved) {
            throw new ValidationException("rejected");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("this booking with id %d is not found",
                        bookingId)));
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException(String.format("booking with id %d is not found", bookingId));
        }
    }

    @Override
    public List<BookingDto> getAllBookingByOwnerId(Integer ownerId, String stringState) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("owner with id %d is not found", ownerId)));
        List<Booking> bookings = bookingRepository.getAllByItemOwnerIdOrderByStartDesc(ownerId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("he has no reservations");
        }
        State state = getState(stringState);
        LocalDateTime localDateTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.getOwnerAll(ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.getOwnerFuture(ownerId, localDateTime);
                break;
            case CURRENT:
                bookings = bookingRepository.getOwnerCurrent(ownerId, localDateTime);
                break;
            case WAITING:
                bookings = bookingRepository.getAllByItemOwnerIdAndStatus(ownerId, Status.WAITING);
                break;
            case PAST:
                bookings = bookingRepository.getOwnerPast(ownerId, localDateTime);
                break;
            case REJECTED:
                bookings = bookingRepository.getAllByItemOwnerIdAndStatus(ownerId, Status.REJECTED);
                break;
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingByUserId(Integer userId, String stringState) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d is not found", userId)));
        List<Booking> bookings = bookingRepository.getAllByBookerIdOrderByStartDesc(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("he has no reservations");
        }
        State state = getState(stringState);
        LocalDateTime localDateTime = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = getAllUser(userId);
                break;
            case PAST:
                bookings = bookingRepository.getByBookerIdStatePast(userId, localDateTime);
                break;
            case WAITING:
                bookings = bookingRepository.getByBookerIdAndStatus(userId, Status.WAITING);
                break;
            case CURRENT:
                bookings = bookingRepository.getByBookerIdStateCurrent(userId, localDateTime);
                break;
            case REJECTED:
                bookings = bookingRepository.getByBookerIdAndStatus(userId, Status.REJECTED);
                break;
            case FUTURE:
                bookings = bookingRepository.getFuture(userId, localDateTime);
                break;
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private static State getState(String stringState) {
        State state;
        if (stringState == null) {
            state = State.ALL;
        } else {
            try {
                state = State.valueOf(stringState);
            } catch (Exception e) {
                throw new ValidationException(String.format("Unknown state: %s", stringState));
            }
        }
        return state;
    }

    private List<Booking> getAllUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d is not found", userId)));
        return bookingRepository.getAllByBookerIdOrderByStartDesc(userId);
    }
}
