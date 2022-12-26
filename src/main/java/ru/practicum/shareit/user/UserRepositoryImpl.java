package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.EmailErrorException;
import ru.practicum.shareit.exceptions.UserNotFoundErrorException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

public class UserRepositoryImpl implements UserRepository {
    private int id = 0;
    private static final HashMap<Integer, User> users = new HashMap<>();


    @Override
    public User createUser(User user) {
        validation(user);

        id++;
        User newUser = new User(id, user.getName(), user.getEmail());
        users.put(id, newUser);
        return newUser;
    }

    @Override
    public User updateUser(User user, int userId) {
        validation(user);
        User newUser = users.get(userId);
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        users.put(userId, newUser);
        return newUser;
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }

    @Override
    public User findUserById(int userId) {
        if (users.get(userId) == null) {
            throw new UserNotFoundErrorException("Пользователь c id " + userId + " не найден");
        }
        return users.get(userId);
    }

    @Override
    public Collection<User> findAll() {

        return users.values();
    }

    public void validation(User user) {
        for (User checkEmail : users.values()) {
            if (checkEmail.getEmail().equals(user.getEmail())) {
                throw new EmailErrorException("Такой email уже используется");
            }

        }
    }

}
