package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Integer id;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String description;

    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private Boolean available;

    private User owner;
//    ItemRequest request;
}
