package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class UserService {
    UserRepositoryImpl repository = new UserRepositoryImpl();


    public User createUser(UserDto userDto) {
        return repository.createUser(UserMapper.fromDtoUser(userDto));
    }

    public User updateUser(UserDto userDto, int userId) {
        return repository.updateUser(UserMapper.fromDtoUser(userDto), userId);
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
