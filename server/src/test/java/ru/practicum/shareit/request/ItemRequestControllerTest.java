package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HEADER = "X-Sharer-User-Id";
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private final User user = new User(1, "UserName1", "user1@mail.com");
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        itemRequest = new ItemRequest(1, "request description", user, LocalDateTime.now());
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @SneakyThrows
    @Test
    void addRequest() {
        when(itemRequestService.addRequest(anyInt(), any())).thenReturn(itemRequest);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("request description"));
    }

    @SneakyThrows
    @Test
    void getOwnerRequest() {
        when(itemRequestService.getOwnerRequest(anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("request description"))
                .andExpect(jsonPath("$[0].requesterId").value(1));
    }

    @SneakyThrows
    @Test
    void getOtherRequests() {
        when(itemRequestService.getOtherRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("request description"))
                .andExpect(jsonPath("$[0].requesterId").value(1));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemRequestService.getRequestById(anyInt(), anyInt())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("request description"))
                .andExpect(jsonPath("$.requesterId").value(1));
    }
}