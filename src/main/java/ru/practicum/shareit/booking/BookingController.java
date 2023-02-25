package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl service;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid Booking booking) {
        return service.createBooking(userId, booking);
    }
    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable int bookingId, @RequestParam boolean approved){
        return service.approveBooking(userId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto getBookingByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable("bookingId") Integer bookingId) {
        return service.getBooking(userId, bookingId);
    }
    @GetMapping()
    public Collection<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        return service.getAllBookings(userId,state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByState(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        return service.getAllBookingsItemsForOwner(userId, state);
    }
}
