package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime start;
    @Future
    @Column(name = "end_date")
    private LocalDateTime end;
    @Column(name = "item_id")
    private int itemId;
    @Column(name = "booker_id")
    private int bookerId;
    @Enumerated
    private Status status;

}
