package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto item) {
        return itemClient.create(userId, item);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId, @RequestBody ItemDto item) {
        return itemClient.update(userId, itemId, item);
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId) {
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> searchItemFromText(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(value = "text") String text,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.searchItem(text, from, size, userId);
    }

    @PostMapping(path = "{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
