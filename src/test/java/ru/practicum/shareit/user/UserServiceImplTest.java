package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.UserNotFoundErrorException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {

        user1 = new User(1, "User1 name", "user1@mail.com");
        user2 = new User(2, "User2 name", "user2@mail.com");
    }

    @Test
    void createUser_whenAllCorrect_userCreated() {

        when(userRepository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = userService.createUser(user1);

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void updateUser_whenAllCorrect_userCreated() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = userService.updateUser(user2, user1.getId());
        assertEquals(1, userDto.getId());
        assertEquals("User2 name", userDto.getName());
        assertEquals("user2@mail.com", userDto.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thrownUserNotFoundErrorException() {
        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> userService.updateUser(user2, 4));

        assertEquals("User с id - 4 не найден", exception.getMessage());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        UserDto userDto = userService.getUserById(user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getUserById_whenUserNotFound_thrownUserNotFoundErrorException() {
        UserNotFoundErrorException exception = assertThrows(UserNotFoundErrorException.class,
                () -> userService.getUserById(4));

        assertEquals("User с id - 4 не найден", exception.getMessage());
    }

    @Test
    void getAll_whenAllCorrect_userCreated() {
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
        Collection<UserDto> userDto = userService.getAll();

        List<UserDto> userDtos = new ArrayList<>(userDto);

        assertEquals(2, userDtos.size());
        assertEquals(1, userDtos.get(0).getId());
        assertEquals("User1 name", userDtos.get(0).getName());
        assertEquals("user1@mail.com", userDtos.get(0).getEmail());
        assertEquals(2, userDtos.get(1).getId());
        assertEquals("User2 name", userDtos.get(1).getName());
        assertEquals("user2@mail.com", userDtos.get(1).getEmail());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(user1.getId());
    }
}