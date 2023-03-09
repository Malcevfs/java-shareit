package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.UserNotFoundErrorException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private User user1;
    private User user2;
    private Item item1;
    private ItemRequest itemRequest1;

    @BeforeEach
    void beforeEach() {

        user1 = new User(1, "User1 name", "user1@mail.com");
        user2 = new User(2, "User2 name", "user2@mail.com");

        item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        itemRequest1 = ItemRequest.builder()
                .id(1)
                .description("Request Description1")
                .requester(user1)
                .created(null)
                .build();
    }

    @Test
    void addRequest_whenAllCorrect_requestAdded() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest1);

        ItemRequest itemRequest = itemRequestService.addRequest(user1.getId(), itemRequest1);

        assertEquals(1, itemRequest.getId());
        assertEquals("Request Description1", itemRequest.getDescription());
        assertEquals(1, itemRequest.getRequester().getId());
        assertNotNull(itemRequest.getCreated());
        verify(itemRequestRepository).save(itemRequest1);
    }

    @Test
    void addRequest_whenUserNotFound_thrownUserNotFoundErrorException() {
        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemRequestService.addRequest(4, itemRequest1));

        assertEquals("User с id - 4 не найден", exception.getMessage());
    }

    @Test
    void getOwnerRequest_whenAllCorrect_requestsGet() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));

        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user1.getId()))
                .thenReturn(List.of(itemRequest1));

        when(itemRepository.findAllByRequestId(item1.getId()))
                .thenReturn(List.of(item1));

        Collection<ItemRequestDto> itemRequests = itemRequestService.getOwnerRequest(user1.getId());
        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>(itemRequests);

        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals(1, itemRequestDtos.get(0).getRequesterId());
    }

    @Test
    void getOwnerRequest_whenUserNotFound_thrownUserNotFoundErrorException() {
        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemRequestService.getOwnerRequest(4));

        assertEquals("User с id - 4 не найден", exception.getMessage());
    }

    @Test
    void getOtherRequests_whenAllCorrect_requestsGet() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        when(itemRequestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest1));
        when(itemRepository.findAllByRequestId(item1.getId()))
                .thenReturn(List.of(item1));

        Collection<ItemRequestDto> itemRequestDto = itemRequestService.getOtherRequests(user2.getId(), 0, 10);
        ArrayList<ItemRequestDto> itemRequestDtos = new ArrayList<>(itemRequestDto);

        assertEquals(1, itemRequestDtos.get(0).getId());
        assertEquals(1, itemRequestDtos.get(0).getRequesterId());
    }

    @Test
    void getOtherRequests_whenUserNotFound_thrownUserNotFoundErrorException() {
        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemRequestService.getOtherRequests(4, 0, 10));

        assertEquals("User с id - 4 не найден", exception.getMessage());
    }

    @Test
    void getRequestById_whenAllCorrect_requestsGet() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequestId(item1.getId()))
                .thenReturn(List.of(item1));

        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(user2.getId(), itemRequest1.getId());

        assertEquals(1, itemRequestDto.getId());
        assertEquals(1, itemRequestDto.getRequesterId());
    }

    @Test
    void getRequestById_whenUserNotFound_thrownUserNotFoundErrorException() {
        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemRequestService.getRequestById(4, itemRequest1.getId()));

        assertEquals("User с id - 4 не найден", exception.getMessage());
    }

    @Test
    void getRequestById_whenItemRequestNotFound_thrownUserNotFoundErrorException() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));

        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemRequestService.getRequestById(user2.getId(), itemRequest1.getId()));

        assertEquals("Request с id - 1 не найден", exception.getMessage());
    }
}