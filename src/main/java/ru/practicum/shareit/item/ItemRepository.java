package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item create(int userId, Item item);

    Item update(int userId, int itemId, Item item);

    Item getItem(int userId, int itemId);

    Collection<Item> getAllItems(int userId);

    Collection<Item> searchItem(int userId, String text);
}
