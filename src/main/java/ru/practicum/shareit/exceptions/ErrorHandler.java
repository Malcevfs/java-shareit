package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final RuntimeException e) {
        log.error("Произошала непредвиденная ошибка: {}", e.getMessage());
        return new ErrorResponse("error", "Произошла непредвиденная ошибка");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleException(final EmailErrorException e) {
        log.error("Ошбика валидации email: {}", e.getMessage());
        return new ErrorResponse("error", "Такой email уже зарегестирован");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final MethodArgumentNotValidException e) {
        log.error("Ошбика валидации: {}", e.getMessage());
        return new ErrorResponse("error", "Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final MissingRequestHeaderException e) {
        log.error("Ошбика заголовка: {}", e.getMessage());
        return new ErrorResponse("error", "Заголовок X-Sharer-User-Id не задан");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(final UserNotFoundErrorException e) {
        log.error("Ошбика пользователя: {}", e.getMessage());
        return new ErrorResponse("error", "Пользователь не найден");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final BadRequestException e) {
        log.error("Ошбика запроса: {}", e.getMessage());
        return new ErrorResponse("error", "Запрос передан не верно");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final ItemAviableErrorException e) {
        log.error("Ошбика предмета: {}", e.getMessage());
        return new ErrorResponse("error", "Предмет не может быть создан или изменен без статуса доступности");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final UnsupportedStateException e) {
        log.error("Ошбика параметра state: {}", e.getMessage());
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS","");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(final OwnerErrorException e) {
        log.error("Ошбика предмета: {}", e.getMessage());
        return new ErrorResponse("error", "Ошибка доступа. Пользоваль из запроса не соответсвует владельцу предмета");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(final ItemNotFoundException e) {
        log.error("Ошбика предмета: {}", e.getMessage());
        return new ErrorResponse("error", "Предмет не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(final BookingNotFoundException e) {
        log.error("Ошбика предмета: {}", e.getMessage());
        return new ErrorResponse("error", "Бронирование не найдено");
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final DateTimeException e) {
        log.error("Ошбика валидации: {}", e.getMessage());
        return new ErrorResponse("error", "Не корректная дата бронирования");
    }
}
