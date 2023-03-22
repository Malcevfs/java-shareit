package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    public void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    public void testHandleRuntimeException() {
        RuntimeException e = mock(RuntimeException.class);
        ErrorResponse expected = new ErrorResponse("error", "Произошла непредвиденная ошибка");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
        ErrorResponse expected = new ErrorResponse("error", "Ошибка валидации");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleMissingRequestHeaderException() {
        MissingRequestHeaderException e = mock(MissingRequestHeaderException.class);
        ErrorResponse expected = new ErrorResponse("error", "Заголовок X-Sharer-User-Id не задан");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleUserNotFoundErrorException() {
        UserNotFoundErrorException e = mock(UserNotFoundErrorException.class);
        ErrorResponse expected = new ErrorResponse("error", "Пользователь не найден");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleBadRequestException() {
        BadRequestException e = mock(BadRequestException.class);
        ErrorResponse expected = new ErrorResponse("error", "Запрос передан не верно");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleItemAviableErrorException() {
        ItemAviableErrorException e = mock(ItemAviableErrorException.class);
        ErrorResponse expected = new ErrorResponse("error", "Предмет не может быть создан или изменен без статуса доступности");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleUnsupportedStateException() {
        UnsupportedStateException e = mock(UnsupportedStateException.class);
        ErrorResponse expected = new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", "");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleOwnerErrorException() {
        OwnerErrorException e = mock(OwnerErrorException.class);
        ErrorResponse expected = new ErrorResponse("error", "Ошибка доступа. Пользоваль из запроса не соответсвует владельцу предмета");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleItemNotFoundException() {
        ItemNotFoundException e = mock(ItemNotFoundException.class);
        ErrorResponse expected = new ErrorResponse("error", "Предмет не найден");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleBookingNotFoundException() {
        BookingNotFoundException e = mock(BookingNotFoundException.class);
        ErrorResponse expected = new ErrorResponse("error", "Бронирование не найдено");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void testHandleDateTimeException() {
        DateTimeException e = mock(DateTimeException.class);
        ErrorResponse expected = new ErrorResponse("error", "Не корректная дата бронирования");
        ErrorResponse actual = errorHandler.handleException(e);
        assertEquals(expected.getDescription(), actual.getDescription());
    }
}