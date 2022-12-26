package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.ItemAviableErrorException;
import ru.practicum.shareit.exceptions.OwnerErrorException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ItemRepositoryImpl implements ItemRepository {
    UserService userService = new UserService();

    private static final HashMap<Integer, Item> items = new HashMap<>();
    private int id = 0;


    @Override
    public Item create(int userId, Item item) {
        if (item.getAvailable() == null) {
            throw new ItemAviableErrorException("Значение доступности не может быть пустым");
        }
        userService.getUserById(userId);
//        validation(userId, item);
        item.setOwner(userId);
        id++;
        item.setId(id);
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public Item update(int userId, int itemId, Item item) {

        Item newItem = items.get(itemId);
        validation(userId, newItem);

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        items.put(itemId, newItem);
        return newItem;
    }

    @Override
    public Item getItem(int userId, int itemId) {
        userService.getUserById(userId);

        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAllItems(int userId) {
        userService.getUserById(userId);

        Collection<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == userId) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public Collection<Item> searchItem(int userId, String text) {
        userService.getUserById(userId);

        Collection<Item> userItem = new ArrayList<>();
        if (text.isBlank()) {
            return userItem;
        }
        for (Item item : items.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    userItem.add(item);
                    continue;
                }
                if (item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    userItem.add(item);
                }
            }
        }
        return userItem;
    }

    public void validation(int userId, Item item) {
        userService.getUserById(userId);

        if (item.getOwner() != userId) {
            throw new OwnerErrorException("Владелец из запроса не соответсвует фактическому");
        }
    }
}
