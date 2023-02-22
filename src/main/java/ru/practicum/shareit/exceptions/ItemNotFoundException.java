package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException (String message) {

        super(message);
        log.error("Ошибка поиска Предмета");
    }
}