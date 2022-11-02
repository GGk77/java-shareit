package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
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
class ItemRequestServiceTest {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;
    private final User user = new User(1, "user1", "user1@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1, "request", user, LocalDateTime.now());

    @Autowired
    public ItemRequestServiceTest(ItemRequestRepository itemRequestRepository, ItemRequestService itemRequestService, UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestService = itemRequestService;
        userService.create(UserMapper.toUserDto(user));
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void createNewRequestTest() {
        ItemRequestDto itemRequest1 = itemRequestService
                .create(itemRequest.getRequester().getId(),
                        ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>()));
        assertEquals(ItemRequestMapper.toItemRequestDto(itemRequestRepository
                .findById(itemRequest1.getId()).orElseThrow(), new ArrayList<>()).getId(), itemRequest1.getId());
    }

    @Test
    void getAllByUserIdTest() {
        assertEquals(itemRequestService.getAllRequestsForUser(2, 0, 11).get(0).getId(),
                List.of(ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>())).get(0).getId());
    }


    @Test
    void getRequestByIdTest() {
        assertEquals(itemRequestService.getItemRequestById(itemRequest.getId(),
                user.getId()).getId(), ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>()).getId());
    }

    @Test
    void getRequestWithNegativeSizeTest() {
        assertThrows(ValidationException.class, () -> itemRequestService.getAllRequestsForUser(2, -10, -10));
    }

    @Test
    void getAllRequestOrderByCreateTest() {
        assertEquals(itemRequestService.getAllItemRequest(user.getId()).get(0).getId(),
                List.of(ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>())).get(0).getId());
    }
}
