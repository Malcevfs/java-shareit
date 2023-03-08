package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    BookingServiceImpl bookingService;

    private LocalDateTime start;
    private LocalDateTime end;
    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;
    private ShortBookingDto shortBookingDto;
    private UserDto userDto2;


    @BeforeEach
    void beforeEach() {

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

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

        booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();

        shortBookingDto = ShortBookingDto.builder()
                .id(1)
                .itemId(item1.getId())
                .start(start)
                .end(end)
                .build();
    }

    @Test
    void createBooking_whenCorrectUser_bookingSaved() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDto bookingDto = bookingService.createBooking(
                userDto2.getId(),
                shortBookingDto);

        assertEquals(1, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item1, bookingDto.getItem());
        assertEquals(user2, bookingDto.getBooker());

    }

    @Test
    void createBooking_whenUserNotFound_thrownUserNotFoundException() {

        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> bookingService.createBooking(13, shortBookingDto));

        assertEquals("User с id - d не найден", exception.getMessage());

    }

    @Test
    void createBooking_whenItemNotFound_thrownItemNotFoundException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(userDto2.getId(),
                        shortBookingDto));

        assertEquals("Item с id - 1  не найден", exception.getMessage());

    }

    @Test
    void createBooking_whenDateIncorrect_thrownDateTimeException() {
        shortBookingDto.setStart(LocalDateTime.now().plusDays(10));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        DateTimeException exception = assertThrows(DateTimeException.class,
                () -> bookingService.createBooking(userDto2.getId(),
                        shortBookingDto));

        assertEquals("Дата начала бронирования не может быть позже даты окончания бронирования",
                exception.getMessage());

    }

    @Test
    void createBooking_whenItemNotAviable_thrownItemAviableErrorException() {
        item1.setAvailable(false);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        ItemAviableErrorException exception = assertThrows(ItemAviableErrorException.class,
                () -> bookingService.createBooking(userDto2.getId(),
                        shortBookingDto));

        assertEquals("Предмет не доступен", exception.getMessage());

    }

    @Test
    void createBooking_whenIncorrectOwner_thrownOwnerErrorException() {

        item1.setOwner(user2);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(userDto2.getId(),
                        shortBookingDto));

        assertEquals("Владелец премета не может забронировать свою вещь", exception.getMessage());

    }

    @Test
    void approveBooking_whenApproveTrue_bookingIsApproved() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));


        BookingDto bookingDto = bookingService.approveBooking(
                user1.getId(),
                booking1.getId(),
                true);

        assertEquals(Status.APPROVED, bookingDto.getStatus());

    }

    @Test
    void approveBooking_whenBookingIsApproved_thrownItemAviableErrorException() {
        booking1.setStatus(Status.APPROVED);

        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));


        ItemAviableErrorException exception = assertThrows(ItemAviableErrorException.class,
                () -> bookingService.approveBooking(user1.getId(),
                        booking1.getId(),
                        true));

        assertEquals("Бронирование уже подтверждено", exception.getMessage());

    }

    @Test
    void approveBooking_whenApproveFalse_bookingIsRejected() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));


        BookingDto bookingDto = bookingService.approveBooking(
                user1.getId(),
                booking1.getId(),
                false);

        assertEquals(Status.REJECTED, bookingDto.getStatus());

        verify(bookingRepository).findById(booking1.getId());
        verify(itemRepository).findById(item1.getId());

    }

    @Test
    void approveBooking_whenBookingNotFound_thrownBookingNotFoundException() {

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(1, 3, true));

        assertEquals("Booking с id - 3  не найден у пользователя с id - 1", exception.getMessage());

    }

    @Test
    void approveBooking_whenItemNotFound_thrownItemNotFoundException() {

        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.approveBooking(user2.getId(), booking1.getId(), true));

        assertEquals("Item с id - 1  не найден", exception.getMessage());

    }

    @Test
    void approveBooking_whenIncorrectOwner_thrownOwnerErrorException() {

        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        OwnerErrorException exception = assertThrows(OwnerErrorException.class,
                () -> bookingService.approveBooking(user2.getId(), booking1.getId(), true));

        assertEquals("Ошибка доступа. Подтвердить бронирование может только владелец предмета",
                exception.getMessage());

    }

    @Test
    void getBooking_whenAllDataIsCorrect_bookingIsPresent() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        BookingDto bookingDto = bookingService.getBooking(user2.getId(), booking1.getId());

        assertEquals(1, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item1, bookingDto.getItem());
        assertEquals(user2, bookingDto.getBooker());

        verify(bookingRepository).findById(booking1.getId());
    }

    @Test
    void getBooking_whenBookingNotFound_thrownBookingNotFoundException() {

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(1, 3));

        assertEquals("Booking с id - 3  не найден у пользователя с id - 1", exception.getMessage());

    }

    @Test
    void getBooking_whenItemNotFound_thrownItemNotFoundException() {

        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.getBooking(user2.getId(), booking1.getId()));

        assertEquals("Item с id - 1  не найден", exception.getMessage());

    }

    @Test
    void getBooking_whenIncorrectOwner_thrownOwnerErrorException() {
        item1.setOwner(user2);
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(booking1));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(item1));

        OwnerErrorException exception = assertThrows(OwnerErrorException.class,
                () -> bookingService.getBooking(user1.getId(), booking1.getId()));

        assertEquals("Ошибка доступа. Получить информацию о бронировании может только владелец предмета или ее арендатор",
                exception.getMessage());

    }

    @Test
    void getAllBookings_whenStateALL_bookingsGet() {

        bookingService.getAllBookings(user1.getId(),
                "ALL",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));

    }

    @Test
    void getAllBookings_whenStatePast_bookingsGet() {

        bookingService.getAllBookings(user1.getId(),
                "PAST",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));

    }

    @Test
    void getAllBookings_whenStateCurrent_bookingsGet() {

        bookingService.getAllBookings(user1.getId(),
                "CURRENT",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));

    }

    @Test
    void getAllBookings_whenStateFuture_bookingsGet() {

        bookingService.getAllBookings(user1.getId(),
                "FUTURE",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));

    }

    @Test
    void getAllBookings_whenStateWaiting_bookingsGet() {

        bookingService.getAllBookings(user1.getId(),
                "WAITING",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));

    }

    @Test
    void getAllBookings_whenStateRejected_bookingsGet() {

        bookingService.getAllBookings(user1.getId(),
                "REJECTED",
                0,
                10);

        verify(bookingRepository)
                .findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));

    }

    @Test
    void getAllBookings_whenStateIncorrect_thrownUnsupportedStateException() {

        UnsupportedStateException exception = assertThrows(UnsupportedStateException.class,
                () -> bookingService.getAllBookings(user1.getId(), "INCORRECT", 0, 10
                ));
        assertEquals("Передан не корректный параметр state - INCORRECT", exception.getMessage());
    }

    @Test
    void getAllBookingsItemsForOwner() {
        when(itemRepository.getAllByOwnerIdOrderByIdAsc(user1.getId()))
                .thenReturn(List.of(item1));

        when(userService.getUserById(user1.getId()))
                .thenReturn(any());

        Pageable pageRequest = PageRequest.of(0, 10);

        bookingService.getAllBookingsItemsForOwner(user1.getId(),
                "REJECTED",
                0,
                10);

        verify(bookingRepository)
                .findAllByItemIdOrderByStartDesc(user1.getId(), pageRequest);
    }
}