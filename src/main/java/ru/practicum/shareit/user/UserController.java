package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService service = new UserService();

    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        return service.createUser(userDto);
    }

    @GetMapping(path = "/{userId}")
    public UserDto getUserById(@PathVariable("userId") Integer userId) {
        return service.getUserById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return service.getAll();
    }

    @PatchMapping(path = "/{userId}")
    public User updateUser(@PathVariable("userId") Integer userId, @RequestBody UserDto userDto) {
        return service.updateUser(userDto, userId);
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        service.deleteUser(userId);
    }

}
