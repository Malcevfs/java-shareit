package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundErrorException extends RuntimeException {

    public UserNotFoundErrorException(String message) {

        super(message);
    }
}
