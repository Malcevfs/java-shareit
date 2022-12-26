package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;

public class ItemService {

    ItemRepositoryImpl itemRepository = new ItemRepositoryImpl();

    public Item create(int userId, ItemDto itemDto) {
        return itemRepository.create(userId, ItemMapper.fromDtoItem(itemDto));
    }


    public Item update(int userId, int itemId, ItemDto itemDto) {

        return itemRepository.update(userId, itemId, ItemMapper.fromDtoItem(itemDto));
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
