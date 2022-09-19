package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    Integer id = 0;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public UserDto getUserById(Integer id) {
        return UserMapper.toUserDto(userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with this id %d not found", id))));
    }

    @Override
    public Set<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toSet());
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.allEmails().contains(userDto.getEmail())) {
            throw new ExistException("This email- {}, has been used and cannot be created");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public User update(UserDto userDto, Integer id) {
        UserDto updatedUser = getUserById(id);
        String updatedEmail = userDto.getEmail();
        if (userRepository.allEmails().contains(updatedEmail) ) {
            throw new ExistException("User with this id- {} and with email- {} is not found and cannot be updated");
        }
        if (updatedEmail != null && !updatedEmail.isBlank()) {
            String oldEmail = updatedUser.getEmail();
            userRepository.allEmails().remove(oldEmail);
            updatedUser.setEmail(updatedEmail);
        }
        String updatedName = userDto.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedUser.setName(updatedName);
        }
        return userRepository.update(UserMapper.toUser(updatedUser), id);
    }

    @Override
    public void delete(Integer id) {
        userRepository.delete(UserMapper.toUser(getUserById(id)));
    }

}
