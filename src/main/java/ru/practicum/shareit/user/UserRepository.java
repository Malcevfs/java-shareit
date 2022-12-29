package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user, int id);

    void deleteUser(int id);

    User findUserById(int id);

    Collection<User> findAll();
}
