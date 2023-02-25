package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking, Item item, User user) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                user,
                booking.getStatus()
        );
    }

    public static Booking fromDtoBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem().getId(),
                bookingDto.getBooker().getId(),
                bookingDto.getStatus()
        );
    }
}
