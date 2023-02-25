package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.Collection;
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl service;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid Item item) {
        return service.createItem(userId, item);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto updateUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId, @RequestBody Item item) {
        return service.update(userId, itemId, item);
    }

    @GetMapping(path = "/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Integer itemId) {
        return service.getItem(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public Collection<ItemDto> searchItemFromText(@RequestParam(value = "text") String text) {
        return service.searchItem(text);
    }
}
