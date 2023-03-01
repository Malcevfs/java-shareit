package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailErrorException extends RuntimeException {

    public EmailErrorException(String message) {

        super(message);
    }
}
