package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.Set;


public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> getUserByEmail(String email);
}
