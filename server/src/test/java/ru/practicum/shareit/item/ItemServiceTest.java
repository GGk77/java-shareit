package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemServiceTest {
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final BookingService bookingService;
    @Autowired
    private final UserService userService = Mockito.mock(UserService.class);
    @Autowired
    private final ItemService itemService;

    private final User owner = new User(1, "user1", "user1@mail.ru");
    private final User user2 = new User(2, "user2", "user2@mail.ru");
    private final Item item = new Item(1, "Дрель", "Простая дрель", true, owner, null, null);
    private final ItemDtoWithBooking itemCommentDto;


    @Autowired
    public ItemServiceTest(CommentRepository commentRepository, UserRepository userRepository, ItemRepository itemRepository,
                           BookingService bookingService, BookingRepository bookingRepository, ItemService itemService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        LocalDateTime localDateTime = LocalDateTime.now();
        itemCommentDto = ItemMapper.toItemDtoWithBooking(
                new ArrayList<>(),
                bookingRepository.getFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), localDateTime),
                bookingRepository.getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(), localDateTime),
                item);
        userRepository.save(owner);
        userRepository.save(user2);
    }

    @Test
    void getItemByIdTest() {
        ItemDto item1 = itemService.create(item.getOwner().getId(), ItemMapper.toItemDto(item));
        assertEquals(itemCommentDto.getId(), itemService.getItemById(item1.getId(), owner.getId()).getId());
    }

    @Test
    void getAllByUserIdTest() {
        assertEquals(List.of(itemCommentDto).get(0).getId(),
                itemService.getAllItems(item.getOwner().getId()).get(0).getId());
    }


    @Test
    void createItemDtoTest() {
        ItemDto item1 = itemService.create(item.getOwner().getId(), ItemMapper.toItemDto(item));
        assertEquals(itemRepository.findById(item.getId()).orElse(null).getId(), item1.getId());
    }

    @Test
    void searchItemByBlankTextTest() {
        assertEquals(new ArrayList<>(),
                itemService.searchItemByQuery(" "));
    }

    @Test
    void searchItemByTextTest() {
        assertEquals(List.of(ItemMapper.toItemDto(item)).get(0).getId(),
                itemService.searchItemByQuery("ель").get(0).getId());
    }

    @Test
    void updateItemTest() {
        Item item = new Item(1, "Дрель", "Простая дрель", true, owner, null, null);
        Item updatedItem = new Item();

        itemService.create(item.getOwner().getId(), ItemMapper.toItemDto(item));
        updatedItem.setAvailable(false);
        updatedItem.setDescription("шуруповерт с батареей");
        updatedItem.setName("шуруповерт");
        userService.getUserById(1);
        itemRepository.findById(1);
        itemService.update(1, ItemMapper.toItemDto(updatedItem),1);
        updatedItem.setId(1);
        updatedItem.setOwner(owner);

        assertEquals(itemRepository.findById(item.getId()).orElse(null).getDescription(),
                updatedItem.getDescription());
        assertEquals(itemRepository.findById(item.getId()).orElse(null).getName(),
                updatedItem.getName());
    }

    @Test
    void createCommentTest() throws InterruptedException {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(4))
                .itemId(item.getId())
                .build();
        Booking booking = BookingMapper.toBooking(user2, item, bookingService.create(bookingDto, user2.getId()));
        bookingService.update(owner.getId(), booking.getId(), true);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        Thread.sleep(10000);
        CommentDto comment = itemService.addComment(user2.getId(), item.getId(), commentDto);
        assertEquals(commentRepository.findById(comment.getId()).orElse(null).getText(), comment.getText());
    }
}