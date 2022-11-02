package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;
    private final User user = new User(1, "user1", "user1@mail.ru");

    private final ItemRequest itemRequest = new ItemRequest(1, "request", user, LocalDateTime.now());
    private final Item item = new Item(1, "item1", "description1", true, user, itemRequest, null);


    @Test
    void createRequestTest() throws Exception {
        when(itemRequestService.create(anyInt(), any()))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequest, List.of(item)));
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    void getAllRequestsByUserTest() throws Exception {
        when(itemRequestService.getAllItemRequest(anyInt()))
                .thenReturn(List.of(ItemRequestMapper.toItemRequestDto(itemRequest, List.of(item))));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(itemRequestService.getAllRequestsForUser(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemRequestMapper.toItemRequestDto(itemRequest, List.of(item))));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(anyInt(), anyInt()))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequest, List.of(item)));
        mockMvc.perform(get("/requests/{id}", itemRequest.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }
}