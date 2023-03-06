package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundErrorException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;


@RequiredArgsConstructor
@Service
public class UserServiceImpl {
    private final UserRepository userRepository;

    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto updateUser(User user, int userId) {
        User newUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x", userId)));

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    public UserDto getUserById(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("Пользователь с id - %x", userId)));
        return UserMapper.toUserDto(user);
    }

    public Collection<UserDto> getAll() {
        Collection<UserDto> userDtos = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        return userDtos;
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
}
