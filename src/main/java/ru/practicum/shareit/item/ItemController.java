package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
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
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId, @RequestBody Item item) {
        return service.update(userId, itemId, item);
    }

    @GetMapping(path = "/{itemId}")
    public ItemBookingDto getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId) {
        return service.getItem(userId, itemId);
    }

    @GetMapping
    public Collection<ItemBookingDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public Collection<ItemDto> searchItemFromText(@RequestParam(value = "text") String text) {
        return service.searchItem(text);
    }

    @PostMapping(path = "{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId,@Valid @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }
}
