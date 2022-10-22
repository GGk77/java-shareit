package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class BookingServiceTest {
    private final BookingRepository bookingRepository;

    private final BookingService bookingService;

    private final User user = new User(1, "user1", "user1@mail.ru");
    private final User user2 = new User(2, "user2", "user2@mail.ru");
    private final Item item = new Item(1, "Дрель", "Простая дрель", true, user, null, null);
    private final Item itemNotAvailable = new Item(1, "Дрель", "Непростая дрель", false, user, null, null);
    private final Booking booking = new Booking(1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, Status.WAITING);
    private final Booking bookingApprove = new Booking(2, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, Status.APPROVED);
    private final Booking bookingReject = new Booking(3, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, Status.REJECTED);

    @Autowired
    public BookingServiceTest(BookingRepository bookingRepository,
                              BookingService bookingService,
                              ItemService itemService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        userService.create(UserMapper.toUserDto(user));
                userService.create(UserMapper.toUserDto(user2));
        itemService.create(item.getOwner().getId(), ItemMapper.toItemDto(item));
        bookingRepository.save(booking);
        bookingRepository.save(bookingApprove);
        bookingRepository.save(bookingReject);
    }

    @Test
    void getBookingByIdTest() {
        assertEquals(booking.getId(),
                bookingService.getBookingById(booking.getId(), booking.getBooker().getId()).getId());
    }

    @Test
    void getWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), 100));
    }

    @Test
    void createBookingTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1)
                .build();
        Booking booking1 = BookingMapper.toBooking(user, item,
                bookingService.create(bookingDto, 2));
        assertEquals(booking1.getId(), bookingRepository.findById(booking1.getId()).orElse(null).getId());
    }

    @Test
    void createBookingStartInThePastTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1)
                .build();

        assertThrows(ValidationException.class, () -> BookingMapper.toBooking(user, item,
                bookingService.create(bookingDto, 2)));
    }

    @Test
    void createBookingStartAfterEndTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1)
                .build();

        assertThrows(ValidationException.class, () -> BookingMapper.toBooking(user, item,
                bookingService.create(bookingDto, 2)));
    }

    @Test
    void createBookingEndInThePastTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().minusDays(2))
                .itemId(1)
                .build();

        assertThrows(ValidationException.class, () -> BookingMapper.toBooking(user, item,
                bookingService.create(bookingDto, 2)));
    }

    @Test
    void createBookingNotAvailableTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1)
                .build();

        assertThrows(NotFoundException.class, () -> BookingMapper.toBooking(user, itemNotAvailable,
                bookingService.create(bookingDto, 1)));
    }

    @Test
    void createBookingByOwnerTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1)
                .build();

        assertThrows(NotFoundException.class, () -> BookingMapper.toBooking(user, itemNotAvailable,
                bookingService.create(bookingDto, 1)));
    }

    @Test
    void updateBookingTest() {
        bookingService.update(booking.getId(), user.getId(), true);
        assertEquals(Status.APPROVED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void updateBooking2Test() {
        bookingService.update(booking.getId(), user.getId(), false);
        assertEquals(Status.REJECTED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void updateBookingAlreadyApprovedTest() {
        assertThrows(NotFoundException.class, () -> bookingService
                .update(bookingApprove.getId(), user.getId(), true));
    }

    @Test
    void updateBookingAlreadyRejectedTest() {
        assertThrows(NotFoundException.class, () -> bookingService
                .update(bookingReject.getId(), user.getId(), false));
    }

    @Test
    void updateBookingApprovedByNotOwnerTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .update(booking.getId(), user2.getId(), true));
    }

    @Test
    void getAllBookingByUserIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByUserId(user2.getId(), "ALL", 0, 10).size());
    }

    @Test
    void getPastBookingByUserIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByUserId(user2.getId(), "PAST", 0, 10));
    }

    @Test
    void getFutureBookingByUserIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByUserId(user2.getId(), "FUTURE", 0, 10));
    }

    @Test
    void getCurrentBookingByUserIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByUserId(user2.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void getWaitingBookingByUserIdTest() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getAllBookingByUserId(user2.getId(), "WAITING", 0, 10).get(0).getId());
    }

    @Test
    void getRejectedBookingByUserIdTest() {
        assertEquals(List.of(bookingReject).get(0).getId(),
                bookingService.getAllBookingByUserId(user2.getId(), "REJECTED", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByUserIdNegativeTest() {
        assertThrows(IllegalArgumentException.class, () -> bookingService
                .getAllBookingByUserId(user2.getId(), "ALL", -1, -1));
    }

    @Test
    void getAllBookingByUserIdBadWithoutBookingTest() {
        assertThrows(NotFoundException.class, () -> bookingService
                .getAllBookingByOwnerId(user2.getId(), "BAD_STATE", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByUserIdBadStateTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .getAllBookingByUserId(user2.getId(), "BAD_STATE", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByOwnerIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByOwnerId(user.getId(), "ALL", 0, 10).size());
    }

    @Test
    void getPastBookingByOwnerIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByOwnerId(user.getId(), "PAST", 0, 10));
    }

    @Test
    void getFutureBookingByOwnerIdTest() {
        assertEquals(new ArrayList<>(),
                bookingService.getAllBookingByOwnerId(user.getId(), "FUTURE", 0, 10));
    }

    @Test
    void getCurrentBookingByOwnerIdTest() {
        assertEquals(3,
                bookingService.getAllBookingByOwnerId(user.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void getWaitingBookingByOwnerIdTest() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getAllBookingByOwnerId(user.getId(), "WAITING", 0, 10).get(0).getId());
    }

    @Test
    void getRejectedBookingByOwnerIdTest() {
        assertEquals(List.of(bookingReject).get(0).getId(),
                bookingService.getAllBookingByOwnerId(user.getId(), "REJECTED", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByOwnerIdNegativeTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .getAllBookingByOwnerId(user.getId(), "ALL", -1, -1));
    }

    @Test
    void getAllBookingByOwnerIdBadStateTest() {
        assertThrows(ValidationException.class, () -> bookingService
                .getAllBookingByOwnerId(user.getId(), "BAD_STATE", 0, 10).get(0).getId());
    }

    @Test
    void getAllBookingByOwnerIdNullStateTest() {
        assertEquals(3,
                bookingService.getAllBookingByOwnerId(user.getId(), null, 0, 10).size());
    }
}