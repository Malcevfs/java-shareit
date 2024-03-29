package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl service;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ShortBookingDto booking) {
        return service.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("bookingId") Integer bookingId, @RequestParam boolean approved) {
        return service.approveBooking(userId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("bookingId") Integer bookingId) {
        return service.getBooking(userId, bookingId);
    }

    @GetMapping()
    public Collection<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllBookings(userId,state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByState(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllBookingsItemsForOwner(userId, state, from, size);
    }
}
