package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByItemIdOrderByStartDesc(int itemId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :start AND b.status != 'REJECTED' ORDER BY b.start ASC, b.status")
    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(int itemId, LocalDateTime start);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, int itemId, LocalDateTime date);

    ArrayList<Booking> findAllByBookerIdAndStatus(Integer bookerId, Status status, Pageable pageable); //от сюда

    ArrayList<Booking> findAllByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime date, Pageable pageable);

    ArrayList<Booking> findAllByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItemIdAndBookerIdAndEndLessThanAndStatus(
            Long id, Long id1, LocalDateTime end, Status status);

    Optional<Booking> findFirstByBookerAndItemIdAndEndBefore(User booker, Integer itemId, LocalDateTime date);

    ArrayList<Booking> findByItemOwnerIdOrderByStartDesc(Integer ownerId, Pageable pageable);

    ArrayList<Booking> findAllByBookerIdOrderByStartDesc(Integer userId, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.booker.id = :bookerId")
    ArrayList<Booking> findByBookerIdCurrDate(Integer bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.end < :date")
    ArrayList<Booking> findAllItemBookingEndIsBefore(Integer ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and " +
            "b.start > :date")
    ArrayList<Booking> findAllItemBookingAndStartIsAfter(Integer ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.start < :date and :date < b.end and " +
            "b.item.owner.id = :ownerId")
    ArrayList<Booking> findAllItemBookingCurrDate(Integer ownerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where  " +
            "b.item.owner.id = :ownerId and b.status = :status")
    ArrayList<Booking> findAllItemBookingStatus(Integer ownerId, Status status, Pageable pageable);

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

    @Query("select b from Booking b where b.item.id in ?1 and b.item.owner.id = ?2 and b.status != 'REJECTED'" +
            " and b.start < ?3 order by b.start desc")
    List<Booking> findByItemIdAndOwnerIdAndStartDateLessThenNowInOrderByIdDesc(
            Collection<Integer> ids, int ownerId, LocalDateTime time);

    @Query("select b from Booking b where b.item.id in ?1 and b.item.owner.id = ?2 and b.status != 'REJECTED'" +
            " and b.start > ?3 order by b.start")
    List<Booking> findByItemIdAndOwnerIdAndStartDateIsMoreThenNowInOrderByIdAsc(
            Collection<Integer> ids, int ownerId, LocalDateTime time);
}
