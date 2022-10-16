package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
            item.setRequest(itemRequest);
        }
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }


    public static ItemDto toItemDto(Item item) {
        Integer requestId = null;
        if (item.getRequest() != null)
            requestId = item.getRequest().getId();
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(List<Comment> commentList, Booking lastBooking,
                                                          Booking nextBooking, Item item) {
        List<ItemDtoWithBooking.Comment> comments = commentList.stream()
                .map(comment -> {
                    ItemDtoWithBooking.Comment comment1 = new ItemDtoWithBooking.Comment();
                    comment1.setId(comment.getId());
                    comment1.setText(comment.getText());
                    comment1.setAuthorName(comment.getUser().getName());
                    comment1.setCreated(comment.getCreated());
                    return comment1;
                }).collect(Collectors.toList());

        ItemDtoWithBooking.Booking lstBooking = new ItemDtoWithBooking.Booking();
        if (lastBooking != null) {
            lstBooking.setId(lastBooking.getId());
            lstBooking.setBookerId(lastBooking.getBooker().getId());
        } else {
            lstBooking = null;
        }

        ItemDtoWithBooking.Booking nxtBooking = new ItemDtoWithBooking.Booking();
        if (nextBooking != null) {
            nxtBooking.setId(nextBooking.getId());
            nxtBooking.setBookerId(nextBooking.getBooker().getId());
        } else {
            nxtBooking = null;
        }
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lstBooking)
                .nextBooking(nxtBooking)
                .comments(comments)
                .build();
    }
}