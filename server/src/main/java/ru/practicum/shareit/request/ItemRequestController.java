package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestServiceImpl service;

    @PostMapping
    public ItemRequest addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemRequest request) {
        return service.addRequest(userId, request);
    }

    @GetMapping
    public Collection<ItemRequestDto> getOwnerRequest(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.getOwnerRequest(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getOtherRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getOtherRequests(userId, from, size);
    }

    @GetMapping(path = "/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("requestId") Integer requestId) {
        return service.getRequestById(userId, requestId);
    }
}
