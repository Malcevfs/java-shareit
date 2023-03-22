package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundErrorException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequest addRequest(int userId, ItemRequest itemRequest) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        return repository.save(itemRequest);
    }

    public Collection<ItemRequestDto> getOwnerRequest(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

        Collection<ItemRequestDto> itemRequests = new ArrayList<>();
        List<Integer> requestId = new ArrayList<>();

        for (ItemRequest item : repository.findAllByRequesterIdOrderByCreatedDesc(userId)) {
            requestId.add(item.getRequester().getId());
            List<ItemDto> items = itemRepository.findByRequest_IdIn(requestId)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(item);
            dto.setItems(items);
            itemRequests.add(dto);
        }
        return itemRequests;
    }

    public Collection<ItemRequestDto> getOtherRequests(int userId, int from, int size) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

        Collection<ItemRequestDto> itemRequests = new ArrayList<>();
        List<Integer> requestId = new ArrayList<>();

        for (ItemRequest item : repository.findAllByRequesterIdIsNotOrderByCreatedDesc(userId, PageRequest.of(from, size))) {
            requestId.add(item.getRequester().getId());

            List<ItemDto> items = itemRepository.findByRequest_IdIn(requestId)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(item);
            dto.setItems(items);
            itemRequests.add(dto);
        }
        return itemRequests;
    }

    public ItemRequestDto getRequestById(int userId, int requestId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("Request с id - %x не найден", requestId)));

        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemRepository.findAllByRequestId(itemRequest.getId())) {
            items.add(ItemMapper.toItemDto(item));
        }
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

}
