package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findAllByItemIdOrderByStartDesc(int itemId);

    List<Booking> findAllByItemIdAndEndBeforeOrderByStartDesc(int itemId, LocalDateTime end);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(int itemId, LocalDateTime start);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, int itemId, LocalDateTime date);
}
