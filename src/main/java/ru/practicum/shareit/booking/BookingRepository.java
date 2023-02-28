package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findAllByItemIdOrderByStartDesc(int itemId);

    @Query("select distinct booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.id = ?1 " +
            "order by booking.start desc ")
    Optional<Booking> findLastBooking(int itemId, LocalDateTime now);

    @Query("select distinct booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.id = ?1 " +
            "order by booking.start ")
    Optional<Booking> findNextBooking(int itemId, LocalDateTime now);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, int itemId, LocalDateTime date);
}
