package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto create(Integer userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d is not found", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsForUser(Integer userId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("the size or from must be greater than 0");
        }
        return itemRequestRepository.getByRequesterIdNot(userId,
                        PageRequest.of(from / size, size, Sort.by("created").descending()))
                .stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.getByRequestId(itemRequest.getId(), Sort.by("id").descending());
                    return ItemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Integer userId, Integer itemRequestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d is not found", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("itemRequest with id %d is not found", itemRequestId)));
        List<Item> items = itemRepository.getByRequestId(itemRequest.getId(), Sort.by("id").descending());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }


    @Override
    public List<ItemRequestDto> getAllItemRequest(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d is not found", userId)));
        return itemRequestRepository.getByRequesterIdOrderByCreatedAsc(userId)
                .stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.getByRequestId(itemRequest.getId(), Sort.by("id").descending());
                    return ItemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

}

