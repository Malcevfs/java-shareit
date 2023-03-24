package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto item) {
        log.info("Create Item by user={}, item name={}", userId, item.getName());
        return itemClient.create(userId, item);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId, @RequestBody ItemDto item) {
        log.info("Update Item by user={}, with itemId={}", userId, itemId);
        return itemClient.update(userId, itemId, item);
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId) {
        log.info("Get Item by user={}, with itemId={}", userId, itemId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Get all Items by user={}", userId);

        return itemClient.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> searchItemFromText(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(value = "text") String text,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        if (text == null || text.isEmpty()) {
            log.info("Item dont get from text. Param text is empty");
            return ResponseEntity.ok(Collections.emptyList());
        }
        log.info("Get Item from text={}, with userId={}", text, userId);
        return itemClient.searchItem(text, from, size, userId);
    }

    @PostMapping(path = "{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("itemId") Integer itemId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Comment added for item={}, with userId={}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
