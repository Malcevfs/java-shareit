package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;

public class ItemService {

    private final ItemRepositoryImpl itemRepository = new ItemRepositoryImpl();

    public ItemDto create(int userId, Item item) {
        return ItemMapper.toItemDto(itemRepository.create(userId, item));
    }

    public ItemDto update(int userId, int itemId, Item item) {

        return ItemMapper.toItemDto(itemRepository.update(userId, itemId, item));
    }

    public ItemDto getItem(int userId, int itemId) {

        return ItemMapper.toItemDto(itemRepository.getItem(userId, itemId));
    }

    public Collection<ItemDto> getAllItems(int userId) {

        Collection<ItemDto> userItems = new ArrayList<>();
        for (Item item : itemRepository.getAllItems(userId)) {
            userItems.add(ItemMapper.toItemDto(item));
        }
        return userItems;
    }

    public Collection<ItemDto> searchItem(int userId, String text) {
        Collection<ItemDto> items = new ArrayList<>();
        for (Item item : itemRepository.searchItem(userId, text)) {
            items.add(ItemMapper.toItemDto(item));
        }
        return items;
    }
}
