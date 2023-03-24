package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemServiceImpl itemService;

    @Autowired
    private MockMvc mockMvc;

    private static final String HEADER = "X-Sharer-User-Id";
    private UserDto userDto1;
    private ItemDto itemDto;
    private ItemBookingDto itemBookingDto;
    private CommentDto commentDto;


    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();

        User user1 = new User(1, "UserName1", "user1@mail.ru");
        userDto1 = UserMapper.toUserDto(user1);

        User user2 = new User(1, "UserName2", "user2@mail.ru");

        Item item1 = Item.builder()
                .id(1)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .request(null)
                .build();
        itemDto = ItemMapper.toItemDto(item1);
        itemBookingDto = ItemMapper.toItemBookingDto(item1);

        Comment comment1 = Comment.builder()
                .id(1)
                .text("Comment1 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build();
        commentDto = CommentMapper.toCommentDto(comment1);
    }

    @SneakyThrows
    @Test
    void createItem() {
        when(itemService.createItem(anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @SneakyThrows
    @Test
    void updateItem() {
        when(itemService.update(anyInt(), anyInt(), any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItem(anyInt(), anyInt()))
                .thenReturn(itemBookingDto);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemBookingDto)));
    }

    @SneakyThrows
    @Test
    void getAllItems() {
        when(itemService.getAllItems(anyInt())).thenReturn(List.of(itemBookingDto));

        mockMvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemBookingDto))));
    }

    @SneakyThrows
    @Test
    void searchItemFromText() {
        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item1")
                        .header(HEADER, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @SneakyThrows

    @Test
    void createComment() {
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
    }
}