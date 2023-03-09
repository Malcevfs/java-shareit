package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private ItemDto itemDto1;
    private Booking booking1;
    private Booking booking2;
    private UserDto userDto2;
    private Comment comment1;
    private CommentDto commentDto1;

    @BeforeEach
    void beforeEach() {

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        user1 = new User(1, "User1 name", "user1@mail.com");
        user2 = new User(2, "User2 name", "user2@mail.com");

        userDto2 = UserMapper.toUserDto(user2);

        item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        itemDto1 = ItemMapper.toItemDto(item1);

        item2 = Item.builder()
                .id(2)
                .name("Item2 name")
                .description("Item2 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        booking2 = Booking.builder()
                .id(2)
                .start(start)
                .end(end)
                .item(item2)
                .booker(user2)
                .status(Status.WAITING)
                .build();

        comment1 = Comment.builder()
                .id(1)
                .text("Text1")
                .item(item1)
                .author(user1)
                .created(LocalDateTime.now())
                .build();

        commentDto1 = CommentMapper.toCommentDto(comment1);
    }

    @Test
    void createItem_whenAllCurrent_itemCreated() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user2));

        ItemDto itemDto = itemService.createItem(
                userDto2.getId(), itemDto1);

        assertEquals(1, itemDto.getId());
        assertEquals("Item1 name", itemDto.getName());
        assertEquals("Item1 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertEquals(0, itemDto.getRequestId());
    }

    @Test
    void createItem_whenAvailableIsNull_thrownItemAviableErrorException() {
        itemDto1.setAvailable(null);

        ItemAviableErrorException exception = assertThrows(ItemAviableErrorException.class,
                () -> itemService.createItem(1, itemDto1));

        assertEquals("Параметр Available не может быть пустым", exception.getMessage());
    }

    @Test
    void createItem_whenUserNotFound_thrownUserNotFoundErrorException() {

        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemService.createItem(4, itemDto1));

        assertEquals("User с id - 4 не найден", exception.getMessage());
    }

    @Test
    void createItem_whenUserItemRequestNOtFound_thrownUserNotFoundErrorException() {
        itemDto1.setRequestId(6);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user1));

        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemService.createItem(user1.getId(), itemDto1));

        assertEquals("ItemRequest с id - 6 не найден", exception.getMessage());
    }

    @Test
    void update_whenAllCorrect_itemUpdated() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);

        ItemDto itemDto = itemService.update(user1.getId(), 1, item2);

        assertEquals(1, itemDto.getId());
        assertEquals("Item2 name", itemDto.getName());
        assertEquals("Item2 description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertEquals(0, itemDto.getRequestId());
    }

    @Test
    void update_whenItemIdIncorrect_thrownOwnerErrorException() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        OwnerErrorException exception = assertThrows(OwnerErrorException.class,
                () -> itemService.update(user1.getId(), 1, item2));

        assertEquals("Item с id - 1  не найден", exception.getMessage());
    }

    @Test
    void update_whenOwnerIncorrect_thrownItemNotFoundException() {
        item1.setOwner(user2);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.update(user1.getId(), 1, item2));

        assertEquals("Вещь для обновления не найдена", exception.getMessage());
    }

    @Test
    void getItem_whenAllCorrect_itemGet() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findAllByItemId(anyInt()))
                .thenReturn(List.of(comment1));
        when(bookingRepository.findAllByItemIdAndEndBeforeOrderByStartDesc(anyInt(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(anyInt(), any()))
                .thenReturn(List.of(booking2));

        ItemBookingDto itemBookingDto = itemService.getItem(user1.getId(), item1.getId());

        assertEquals(1, itemBookingDto.getId());
        assertEquals("Item1 name", itemBookingDto.getName());
        assertEquals("Item1 description", itemBookingDto.getDescription());
        assertEquals(true, itemBookingDto.getAvailable());
        assertEquals(booking1.getId(), itemBookingDto.getLastBooking().getId());
        assertEquals(booking1.getStatus(), itemBookingDto.getLastBooking().getStatus());
        assertEquals(booking2.getId(), itemBookingDto.getNextBooking().getId());
        assertEquals(booking2.getStatus(), itemBookingDto.getNextBooking().getStatus());
        assertEquals(1, itemBookingDto.getComments().size());
        assertEquals("Text1", itemBookingDto.getComments().get(0).getText());
    }

    @Test
    void getItem_whenItemNotFound_ItemNotFoundException() {
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItem(user1.getId(), 5));

        assertEquals("Item с id - 5  не найден", exception.getMessage());
    }

    @Test
    void getAllItems_whenTwoItemsCorrect_getTwoItems() {
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(user1.getId()))
                .thenReturn(List.of(item1, item2));

        Collection<ItemBookingDto> itemBookingDto = itemService.getAllItems(user1.getId());
        assertEquals(2, itemBookingDto.size());
    }

    @Test
    void getAllItems_whenEmptyItems_getEmptyData() {
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(user1.getId()))
                .thenReturn(List.of());

        Collection<ItemBookingDto> itemBookingDto = itemService.getAllItems(user1.getId());
        assertEquals(0, itemBookingDto.size());
    }

    @Test
    void searchItem_whenAllCorrect_getItem() {

        when(itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item1));
        Collection<ItemDto> itemBookingDto = itemService.searchItem("Item1 description", 0, 10);
        List<ItemDto> itemDto = new ArrayList<>(itemBookingDto);
        assertEquals(1, itemBookingDto.size());
        assertEquals(1, itemDto.get(0).getId());
        assertEquals("Item1 name", itemDto.get(0).getName());
        assertEquals("Item1 description", itemDto.get(0).getDescription());
    }

    @Test
    void searchItem_whenTextEmpty_getEmptyList() {
        Collection<ItemDto> itemBookingDto = itemService.searchItem("", 0, 10);
        assertEquals(0, itemBookingDto.size());
    }

    @Test
    void addComment_whenAllCorrect_getComment() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.findItemByIdAndAvailableTrue(anyInt()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findFirstByBookerAndItemIdAndEndBefore(any(), anyInt(), any()))
                .thenReturn(Optional.of(booking1));

        CommentDto commentDto = itemService.addComment(user1.getId(), item1.getId(), commentDto1);

        assertEquals("Text1", commentDto.getText());
        assertEquals("User1 name", commentDto.getAuthorName());
        assertEquals(1, commentDto.getId());
        assertNotNull(commentDto.getCreated());
    }

    @Test
    void addComment_whenUserNotFound_thrownUserNotFoundErrorException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> itemService.addComment(user1.getId(), 5, commentDto1));

        assertEquals("User с id - 1 не найден", exception.getMessage());
    }

    @Test
    void addComment_whenItemNotFound_thrownItemNotFoundException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findItemByIdAndAvailableTrue(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(user1.getId(), 5, commentDto1));

        assertEquals("Item с id - 5  не найден", exception.getMessage());
    }

    @Test
    void addComment_whenBookingNotFound_thrownBadRequestException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));

        when(itemRepository.findItemByIdAndAvailableTrue(anyInt()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findFirstByBookerAndItemIdAndEndBefore(any(), anyInt(), any()))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.addComment(user1.getId(), 5, commentDto1));

        assertEquals("Предмет не был забронирован", exception.getMessage());
    }
}
