package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;

    @NotNull(groups = {Create.class})
    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class})
    private String email;

}
