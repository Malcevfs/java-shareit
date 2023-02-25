package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemAviableErrorException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.OwnerErrorException;
import ru.practicum.shareit.exceptions.UserNotFoundErrorException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto createItem(int userId, Item item) {
        User newUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

        if (item.getAvailable() == null) {
            throw new ItemAviableErrorException("Параметр Available не может быть пустым");
        }
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto update(int userId, int itemId, Item item) {
        Item newItem = itemRepository.findItemByOwnerAndId(userId, itemId).orElseThrow(() ->
                new OwnerErrorException(String.format("Item с id - %x  не найден у пользователя с id - %x", itemId, userId)));

        User newUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    public ItemDto getItem(int itemId) {
        Item newItem = itemRepository.findItemByIdAndAvailableTrue(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id - %x  не найден", itemId)));
        return ItemMapper.toItemDto(newItem);
    }

    public Collection<ItemDto> getAllItems(int userId) {
        User newUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

        Collection<ItemDto> userItems = new ArrayList<>();
        for (Item item : itemRepository.findItemsByOwner(userId)) {
            userItems.add(ItemMapper.toItemDto(item));
        }
        return userItems;
    }

    public Collection<ItemDto> searchItem(String text) {
        Collection<ItemDto> items = new ArrayList<>();
        if(text.isEmpty()){
            return items;
        }
        for (Item item : itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text)) {
            items.add(ItemMapper.toItemDto(item));
        }
        return items;
    }
}


