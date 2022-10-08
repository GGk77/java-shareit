package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Collection<Item> findByOwnerIdOrderByIdAsc(Integer userId);

    @Query("select i from Item i where i.available = true " +
            " and (lower(i.name) like lower(concat('%',:query,'%')) " +
            " or lower(i.description) like lower(concat('%',:query,'%')))")
    Collection<Item> searchByQuery(@Param("query") String query);
}

