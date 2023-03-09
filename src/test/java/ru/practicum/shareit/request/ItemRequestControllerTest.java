package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(controllers = UserController.class)
class ItemRequestControllerTest {
private ItemRequestDto itemRequestDto;
    private User user = new User(1, "UserName1", "user1@mail.com");
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestServiceImpl itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = ItemRequestMapper.toItemRequestDto(new ItemRequest(1, "request description", user, LocalDateTime.now()));
    }

    @Test
    void addRequest() {
    }

    @Test
    void getOwnerRequest() {
    }

    @Test
    void getOtherRequests() {
    }

    @Test
    void getItemById() {
    }
}