package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void before() {
        user1 = userRepository.save(new User(1, "user1", "user1@email"));
        user2 = userRepository.save(new User(2, "user2", "user2@email"));
        item1 = itemRepository.save(new Item(1, "item1", "description1", true, user1, null, null));
        item2 = itemRepository.save(new Item(2, "item2", "description2", true, user2, null, null));
    }

    @AfterEach
    void after() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getByOwnerTest() {
        final List<Item> byOwner = (List<Item>) itemRepository.findByOwnerIdOrderByIdAsc(user1.getId());
        assertNotNull(byOwner);
        assertEquals(1, byOwner.size());
        assertEquals("item1", byOwner.get(0).getName());
    }

    @Test
    void getByTextTest() {
        Collection<Item> itemList = itemRepository.searchByQuery("ptio");
        assertThat(itemList.size(), is(2));
    }
}