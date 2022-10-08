package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exceptions.ExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto getUserById(Integer id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with this id %d not found", id))));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() != null && userDto.getEmail().contains("@")) {
            try {
                User createdUser = userRepository.save(UserMapper.toUser(userDto));
                return UserMapper.toUserDto(createdUser);
            } catch (ExistException e ) {
                throw new ExistException("User with email exists");
            }
        } else {
            throw new ValidationException("Email not found");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Integer id) {
        User updatedUser = UserMapper.toUser(getUserById(id));
        String updatedEmail = userDto.getEmail();
        if (userRepository.getUserByEmail(updatedEmail).isPresent()) {
            throw new ExistException("User with this id- {} and with email- {} is not found and cannot be updated");
        }
        if (updatedEmail != null && !updatedEmail.isBlank()) {
            updatedUser.setEmail(updatedEmail);
        }
        String updatedName = userDto.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedUser.setName(updatedName);
        }
        userRepository.save(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }



}
