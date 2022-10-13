package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public ItemDtoWithBooking getItemById(Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with this id %d is not found", itemId)));
        List<Comment> commentList = getCommentsByItemId(item);
        Integer ownerId = item.getOwner().getId();
        if (ownerId.equals(userId)) {
            LocalDateTime localDateTime = LocalDateTime.now();
            Booking lastBooking = bookingRepository.getFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, localDateTime);
            Booking nextBooking = bookingRepository.getTopByItemIdAndStartAfterOrderByStartAsc(itemId, localDateTime);
            return ItemMapperWithBooking.toItemDtoWithBooking(commentList, lastBooking, nextBooking, item);
        } else return ItemMapperWithBooking.toItemDtoWithBooking(commentList, null, null, item);
    }

    @Override
    public List<ItemDtoWithBooking> getAllItems(Integer ownerId) {
        return itemRepository.findByOwnerIdOrderByIdAsc(ownerId)
                .stream()
                .map(item -> {
                            List<Comment> comments = getCommentsByItemId(item);
                            Booking lastBooking = bookingRepository
                                    .getFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
                            Booking nextBooking = bookingRepository
                                    .getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
                            return ItemMapperWithBooking.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        if (itemDto.getDescription() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("name is empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("description is empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("available is empty");
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d is not found", userId)));
        Item item = ItemMapper.toItem(itemDto, owner);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d is not found", userId)));
        Item item = ItemMapper.toItem(itemDto, owner);
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d is not found", itemId)));
        String newDescription = item.getDescription();
        if (newDescription != null && !newDescription.isBlank()) {
            updatedItem.setDescription(newDescription);
        }
        String newName = item.getName();
        if (newName != null && !newName.isBlank()) {
            updatedItem.setName(newName);
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        if (!updatedItem.getOwner().getId().equals(userId) && owner != null) {
            throw new NotFoundException("item is unavailable");
        }
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public List<ItemDto> searchItemByQuery(String query) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchByQuery(query)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidationException("This comment is empty or blank");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException(String.format("User with id %d is not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidationException(String.format("Item with id %d is not found", itemId)));
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        List<Booking> booking = bookingRepository.getByBookerIdStatePast(comment.getUser().getId(), LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new ValidationException("The user has not booked anything");
        }
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    public List<Comment> getCommentsByItemId(Item item) {
        return commentRepository.getByItem_IdOrderByCreatedDesc(item.getId());
    }

}
