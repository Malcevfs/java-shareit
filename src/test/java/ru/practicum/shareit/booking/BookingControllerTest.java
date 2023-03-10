package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingServiceImpl bookingService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private UserDto user2Dto;
    private BookingDto bookingDto;
    private BookingDto bookingDtoResponse;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user1 = new User(1, "User1 name", "user1@mail.com");
        User user2 = new User(2, "User2 name", "user2@mail.com");
        user2Dto = UserMapper.toUserDto(user2);

        Item item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();

        Booking booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        bookingDto = BookingMapper.toBookingDto(booking1);
        bookingDtoResponse = BookingMapper.toBookingDto(booking1);
    }

    @SneakyThrows
    @Test
    void createBooking() {
        when(bookingService.createBooking(anyInt(), any())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk());
    }

//    @SneakyThrows
//    @Test
//    void approveBooking() {
//        when(bookingService.approveBooking(anyInt(), anyInt(), any(Boolean.class))).thenReturn(bookingDto);
//
//        mockMvc.perform(patch("/bookings/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header(HEADER, 1)
//                        .content(mapper.writeValueAsString(bookingDto)))
//                .andExpect(status().isOk());
//    }

    @SneakyThrows
    @Test
    void getBookingByOwnerId() {
        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoResponse)));
    }

    @SneakyThrows
    @Test
    void getAllBookings() {
        when(bookingService.getAllBookings(anyInt(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoResponse))));
    }

    @SneakyThrows
    @Test
    void getBookingsByState() {
        when(bookingService.getAllBookingsItemsForOwner(anyInt(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(HEADER, user2Dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoResponse))));
    }
}