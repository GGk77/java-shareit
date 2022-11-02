package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private final User user = new User(1, "User1", "user1@mail.ru");
    private final User user2 = new User(2, "User2", "user2@mail.ru");
    private final Item item = new Item(1, "item1", "description1", true, user, null, null);
    private final Booking booking = new Booking(1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, user2, Status.WAITING);

    private final BookingDto bookingDto = BookingMapper.toBookingDto(booking);

    @Test
    void getByIdOk() throws Exception {
        when(bookingService.getBookingById(any(), any())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/{id}", booking.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Integer.class));
    }

    @Test
    void createBookingOk() throws Exception {
        when(bookingService.create(any(), anyInt())).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())));
    }

    @Test
    void updateStatusOk() throws Exception {
        booking.setStatus(Status.APPROVED);
        when(bookingService.update(any(), any(), anyBoolean()))
                .thenReturn(BookingMapper.toBookingDto(booking));

        mockMvc.perform(patch("/bookings/{id}", booking.getId())
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void getAllByUserOk() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyInt(), any(), anyInt(), anyInt())).thenReturn(Collections.singletonList(bookingDto));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))

                .andExpect(status().isOk());
    }

    @Test
    void getAllByOwnerOk() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyInt(), any(), anyInt(), anyInt())).thenReturn(Collections.singletonList(bookingDto));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))

                .andExpect(status().isOk());
    }
}
