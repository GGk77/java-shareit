package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.Set;



public interface UserService {

    UserDto getUserById(Integer id);

    Set<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    User update(UserDto userDto, Integer id);

    void delete(Integer id);

}
