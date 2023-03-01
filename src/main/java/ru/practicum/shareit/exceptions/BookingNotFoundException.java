package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {

        super(message);
    }
}

