package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Transactional
    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);
    @Transactional
    List<Booking> findAllByItemIdOrderByStartDesc(int itemId);
}
