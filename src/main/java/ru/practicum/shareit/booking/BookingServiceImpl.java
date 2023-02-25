package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.*;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

        public BookingDto createBooking (int userId, Booking booking){
            Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() ->
                    new ItemNotFoundException(String.format("Item с id - %x  не найден", booking.getItemId())));
            User user = userRepository.findById(userId).orElseThrow(() ->
                    new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

            if(booking.getStart().isAfter(booking.getEnd())){
                throw new DateTimeException("Дата начала бронирования не может быть позже даты окончания бронирования");
            }
            if(item != null){
               if(!item.getAvailable()){
                   throw new ItemAviableErrorException("Предмет не доступен");
               }
            }
            booking.setBookerId(userId);
            booking.setStatus(Status.WAITING);
            return BookingMapper.toBookingDto(bookingRepository.save(booking),item,user);
        }
        @Transactional
        public BookingDto approveBooking(int userId, int bookingId, boolean approve){

            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new BookingNotFoundException(String.format("Booking с id - %x  не найден " +
                            "у пользователя с id - %x", bookingId, userId)));
            Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() ->
                    new ItemNotFoundException(String.format("Item с id - %x  не найден", booking.getItemId())));
            if (item.getOwner() != userId){
                throw new OwnerErrorException("Ошибка доступа. Подтвердить бронирование может только владелец предмета");
            }
            User user = userRepository.findById(booking.getBookerId()).orElseThrow(() ->
                    new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));
            if(approve){
                booking.setStatus(Status.APPROVED);
                bookingRepository.saveAndFlush(booking);
            }
            if(!approve){
                booking.setStatus(Status.REJECTED);
                bookingRepository.saveAndFlush(booking);
            }
            return BookingMapper.toBookingDto(booking, item, user);
        }

        public BookingDto getBooking(int userId, int bookingId){
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new BookingNotFoundException(String.format("Booking с id - %x  не найден " +
                            "у пользователя с id - %x", bookingId, userId)));
            Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() ->
                    new ItemNotFoundException(String.format("Item с id - %x  не найден", booking.getItemId())));
            User user = userRepository.findById(userId).orElseThrow(() ->
                    new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

            if ((item.getOwner() != userId) && (booking.getBookerId() != userId)){
                throw new OwnerErrorException("Ошибка доступа. " +
                        "Получить информацию о бронировании может только владелец предмета или ее арендатор");
            }
            return BookingMapper.toBookingDto(booking, item, user);
        }

        public Collection<BookingDto> getAllBookings(int userId, String state){
            ArrayList<BookingDto> bookings = new ArrayList<>();
            List<BookingDto> filteredBookings;

            User user = userRepository.findById(userId).orElseThrow(() ->
                    new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));

            for (Booking booking : bookingRepository.findAllByBookerIdOrderByStartDesc(userId)) {
                Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() ->
                        new ItemNotFoundException(String.format("Item с id - %x  не найден", booking.getItemId())));
                bookings.add(BookingMapper.toBookingDto(booking,item, user));
            }
            switch (state){
                case "CURRENT" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "PAST" -> {
                     filteredBookings = bookings.stream().filter(booking -> booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "FUTURE" -> {
                     filteredBookings = bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "WAITING" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getStatus().equals(Status.WAITING)).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "REJECTED" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getStatus().equals(Status.REJECTED)).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "ALL" -> {
                    return bookings;
                }
                default -> throw new UnsupportedStateException ("Передан не корректный параметр state - " + state);
            }

        }

        public Collection<BookingDto> getAllBookingsItemsForOwner(int userId, String state){ArrayList<BookingDto> bookings = new ArrayList<>();
            List<BookingDto> filteredBookings;

            User user = userRepository.findById(userId).orElseThrow(() ->
                    new UserNotFoundErrorException(String.format("User с id - %x не найден", userId)));
            List<Item> itemsByOwner = itemRepository.findItemsByOwner(userId);

                for( Item item : itemsByOwner) {
                    for (Booking booking : bookingRepository.findAllByItemIdOrderByStartDesc(item.getId())) {
                        bookings.add(BookingMapper.toBookingDto(booking, item, user));
                    }
                }

            switch (state){
                case "CURRENT" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "PAST" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "FUTURE" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "WAITING" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getStatus().equals(Status.WAITING)).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "REJECTED" -> {
                    filteredBookings = bookings.stream().filter(booking -> booking.getStatus().equals(Status.REJECTED)).collect(Collectors.toList());
                    return filteredBookings;
                }
                case "ALL" -> {
                    return bookings;
                }
                default -> throw new UnsupportedStateException ("Передан не корректный параметр state - " + state);
            }}

}
