package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemAviableErrorException extends RuntimeException {

    public ItemAviableErrorException(String message) {

        super(message);
        log.error("Ошибка доступности предмета");
    }
}
