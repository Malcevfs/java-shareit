package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemRequestDto request) {
        log.info("Add request for item={}, by user={}", request.getItems(), userId);
        return requestClient.addRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequest(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Get owner request for user={}", userId);
        return requestClient.getOwnerRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all requests for user={}", userId);
        return requestClient.getOtherRequests(userId, from, size);
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("requestId") Integer requestId) {
        log.info("Get requests by id={}, for user={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
