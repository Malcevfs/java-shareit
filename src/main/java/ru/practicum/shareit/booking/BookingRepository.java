package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByItemOwnerIdOrderByStartDesc(int ownerId, Pageable pageable);

    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId, Pageable pageable);

    List<Booking> findAllByItemIdOrderByStartDesc(int itemId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.end < :end AND b.status != 'REJECTED' ORDER BY b.start DESC, b.status")
    List<Booking> findAllByItemIdAndEndBeforeOrderByStartDesc(int itemId, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :start AND b.status != 'REJECTED' ORDER BY b.start ASC, b.status")
    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(int itemId, LocalDateTime start);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, int itemId, LocalDateTime date);
}
