package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;



public interface UserService {

    UserDto getUserById(Integer id);

    List<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Integer id);

    void delete(Integer id);

}
