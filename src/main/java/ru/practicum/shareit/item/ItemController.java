package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl service;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto item) {
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
    public Collection<ItemBookingDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllItems(userId, from, size);
    }

    @GetMapping(path = "/search")
    public Collection<ItemDto> searchItemFromText(@RequestParam(value = "text") String text,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.searchItem(text, from, size);
    }

    @PostMapping(path = "{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId,@Valid @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }
}
