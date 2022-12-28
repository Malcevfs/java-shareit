package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryImpl repository = new UserRepositoryImpl();


    public UserDto createUser(User user) {
        return UserMapper.toUserDto(repository.createUser(user));
    }

    public UserDto updateUser(User user, int userId) {
        return UserMapper.toUserDto(repository.updateUser(user, userId));
    }

    public UserDto getUserById(int userId) {
        return UserMapper.toUserDto(repository.findUserById(userId));
    }

    public Collection<UserDto> getAll() {
        Collection<UserDto> userDtos = new ArrayList<>();
        for (User user : repository.findAll()) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        return userDtos;
    }

    public void deleteUser(int userId) {
        repository.deleteUser(userId);
    }

}
