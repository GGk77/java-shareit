package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    Integer id;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    String name;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    String description;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    Boolean available;

    User owner;
//    ItemRequest request;
}
