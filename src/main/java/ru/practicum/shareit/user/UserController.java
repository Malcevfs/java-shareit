package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl service;
    @PostMapping
    public UserDto createUser(@RequestBody @Valid User user) {
        return service.createUser(user);
    }

    @PatchMapping(path = "/{userId}")
    public UserDto updateUser(@PathVariable("userId") Integer userId, @RequestBody User user) {
        return service.updateUser(user, userId);
    }

    @GetMapping(path = "/{userId}")
    public UserDto getUserById(@PathVariable("userId") Integer userId) {
        return service.getUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return service.getAll();
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        service.deleteUser(userId);
    }
}
