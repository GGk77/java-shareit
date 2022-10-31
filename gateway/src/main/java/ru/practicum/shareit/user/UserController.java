package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Valid
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create new user={}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Integer id) {
        log.info("Update user={}", userDto);
        return userClient.updateUser(id, userDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Integer id) {
        log.info("Get user with id={}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> retrieveAllUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable Integer id) {
        log.info("Remove user with ID = {}", id);
        userClient.removeUser(id);
    }

}