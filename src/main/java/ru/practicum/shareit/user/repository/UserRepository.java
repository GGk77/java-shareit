package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.Set;


public interface UserRepository {

    Optional<User> getUserById(Integer id);

    Set<User> getAllUsers();

    User create(User user);

    User update(User user, Integer id);

    void delete(User user);

    Set<String> allEmails();
}
