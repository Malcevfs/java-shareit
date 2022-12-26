package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OwnerErrorException extends RuntimeException {

    public OwnerErrorException(String message) {

        super(message);
        log.error("Ошибка владельца предмета");
    }
}
