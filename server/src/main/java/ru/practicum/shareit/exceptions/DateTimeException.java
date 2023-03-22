package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeException extends RuntimeException {

    public DateTimeException(String message) {

        super(message);
    }
}