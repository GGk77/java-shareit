package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.Set;

public interface ItemRepository {

    Optional<Item> getItemById(Integer id);

    Set<Item> getAllItems();

    Item create(Item Item);

    Item update(Item Item, Integer id);

    Set<Item> searchItemByQuery(String query);

    Set<Item> getAllItemsByUserId(Integer userId);


}
