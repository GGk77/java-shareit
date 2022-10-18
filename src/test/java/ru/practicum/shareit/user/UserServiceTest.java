package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final User user1;
    private final User user2;
    private final User user3 = new User(3, "user3", "user2@mail.ru");

    @Autowired
    public UserServiceTest(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
        userRepository.save(user1 = new User(1, "user1", "user1@mail.ru"));
        userRepository.save(user2 = new User(2, "user2", "user2@mail.ru"));
    }


    @Test
    void createUserTest() {
        UserDto newUser = userService.create(UserMapper.toUserDto(user1));
        assertEquals(userService.getUserById(newUser.getId()).getId(), user1.getId());
    }

    @Test
    void updateUserTest() {
        UserDto userDto = userService.getUserById(1);
        userDto.setName("test");
        userDto.setEmail("test@test.ru");
        userService.update(userDto, 1);
        assertEquals("test", userService.getUserById(1).getName());
        assertEquals("test@test.ru", userService.getUserById(1).getEmail());
    }

    @Test
    void getUserByIdTest() {
        assertEquals(UserMapper.toUserDto(user1).getId(), userService.getUserById(1).getId());
    }


    @Test
    void deleteUserByIdTest() {
        userService.delete(1);
        assertNull(userRepository.findById(1).orElse(null));
    }

    @Test
    void getAllUsersTest() {
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void createUserWithDuplicateEmailTest() {
        userService.create(UserMapper.toUserDto(user2));
        final ExistException exception = assertThrows(ExistException.class, () -> userService.create(UserMapper.toUserDto(user3)));
        assertEquals("User with email exists", exception.getMessage());
    }
}
