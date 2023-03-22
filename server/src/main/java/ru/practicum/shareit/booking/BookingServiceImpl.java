package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;


    public BookingDto createBooking(int userId, ShortBookingDto shortBookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

        Item item = itemRepository.findById(shortBookingDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id - %x  не найден", shortBookingDto.getId())));

        if (item.getOwner().getId() == userId) {
            throw new ItemNotFoundException("Владелец премета не может забронировать свою вещь");
        }

        if (shortBookingDto.getEnd().isBefore(shortBookingDto.getStart()) ||
                shortBookingDto.getStart().equals(shortBookingDto.getEnd())) {
            throw new DateTimeException("Дата начала бронирования не может быть позже даты окончания бронирования");
        }
        if (!item.getAvailable()) {
            throw new ItemAviableErrorException("Предмет не доступен");
        }
        Booking booking = BookingMapper.shortBookingDtoToBooking(shortBookingDto, item, user);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto approveBooking(int userId, int bookingId, boolean approve) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Booking с id - %x  не найден " +
                        "у пользователя с id - %x", bookingId, userId)));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id - %x  не найден", booking.getItem().getId())));
        if (item.getOwner().getId() != userId) {
            throw new OwnerErrorException("Ошибка доступа. Подтвердить бронирование может только владелец предмета");
        }

        if (approve) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ItemAviableErrorException("Бронирование уже подтверждено");
            }
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
        }
        if (!approve) {
            booking.setStatus(Status.REJECTED);
            bookingRepository.save(booking);
        }
        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto getBooking(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Booking с id - %x  не найден " +
                        "у пользователя с id - %x", bookingId, userId)));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item с id - %x  не найден", booking.getItem().getId())));

        if ((item.getOwner().getId() != userId) && (booking.getBooker().getId() != userId)) {
            throw new OwnerErrorException("Ошибка доступа. " +
                    "Получить информацию о бронировании может только владелец предмета или ее арендатор");
        }
        return BookingMapper.toBookingDto(booking);
    }

    public Collection<BookingDto> getAllBookings(int userId, String state, int from, int size) {
        ArrayList<Booking> bookings = new ArrayList<>();
        userService.getUserById(userId);
        int page = from / size;
        Pageable pageRequest = PageRequest.of(page, size);
        Pageable pageable = PageRequest.of(from, size);
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdCurrDate(userId,  LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, pageRequest);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
//        for (Booking booking : bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest)) {
//            bookings.add(BookingMapper.toBookingDto(booking));
//        }
//        return getBookingDtos(state, bookings);

//    }

    public Collection<BookingDto> getAllBookingsItemsForOwner(int userId, String state, int from, int size) {
        ArrayList<Booking> bookings = new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size);

        userService.getUserById(userId);
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllItemBookingEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllItemBookingAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllItemBookingCurrDate(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllItemBookingStatus(userId, Status.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllItemBookingStatus(userId, Status.REJECTED, pageable);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
//        List<Item> itemsByOwner = itemRepository.getAllByOwnerIdOrderByIdAsc(userId);
//
//        for (Item item : itemsByOwner) {
//            for (Booking booking : bookingRepository.findAllByItemIdOrderByStartDesc(item.getId(), PageRequest.of(from, size))) {
//                userService.getUserById(booking.getBooker().getId());
//                bookings.add(BookingMapper.toBookingDto(booking));
//            }
//        }
//
//        return getBookingDtos(state, bookings);
//    }

//    private Collection<BookingDto> getBookingDtos(String state, ArrayList<BookingDto> bookings) {
//        List<BookingDto> filteredBookings;
//
//        if (state.equals("CURRENT")) {
//            filteredBookings = bookings.stream().filter(booking -> (LocalDateTime.now()).isAfter(booking.getStart()) && (LocalDateTime.now()).isBefore(booking.getEnd())).collect(Collectors.toList());
//
//            return filteredBookings;
//        }
//        if (state.equals("PAST")) {
//
//            filteredBookings = bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
//            return filteredBookings;
//        }
//        if (state.equals("FUTURE")) {
//
//            filteredBookings = bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
//            return filteredBookings;
//        }
//        if (state.equals("WAITING")) {
//
//            filteredBookings = bookings.stream().filter(booking -> booking.getStatus().equals(Status.WAITING)).collect(Collectors.toList());
//            return filteredBookings;
//        }
//        if (state.equals("REJECTED")) {
//
//            filteredBookings = bookings.stream().filter(booking -> booking.getStatus().equals(Status.REJECTED)).collect(Collectors.toList());
//            return filteredBookings;
//        }
//        if (state.equals("ALL")) {
//            return bookings;
//        } else {
//            throw new UnsupportedStateException("Передан не корректный параметр state - " + state);
//        }
//    }
}
