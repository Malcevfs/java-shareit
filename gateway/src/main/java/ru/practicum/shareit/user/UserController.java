package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto user) {
        log.info("Creating user {}", user.getName());
        return userClient.create(user);
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") int userId, @RequestBody UserDto user) {
        log.info("Update user with id {},", user.getId());
        return userClient.update(userId, user);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") int userId) {
        log.info("Get user with id {},", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable int userId) {
        log.info("Delete user with id {}", userId);
        return userClient.delete(userId);
    }

}
