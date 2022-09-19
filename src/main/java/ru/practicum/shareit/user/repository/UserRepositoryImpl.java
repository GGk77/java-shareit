package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;


import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    Map<Integer, User> usersMap = new HashMap<>();
    Set<String> emails = new HashSet<>();
    Integer id = 0;

    @Override
    public Optional<User> getUserById(Integer id) {
        return Optional.ofNullable(usersMap.get(id));
    }

    @Override
    public Set<User> getAllUsers() {
        return new HashSet<>(usersMap.values());
    }

    @Override
    public User create(User user) {
        emails.add(user.getEmail());
        user.setId(++id);
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, Integer id) {
        usersMap.put(id, user);
        return user;
    }

    @Override
    public void delete(User user) {
        emails.remove(user.getEmail());
        usersMap.remove(user.getId());
    }

    @Override
    public Set<String> allEmails() {
        return emails;
    }
}
