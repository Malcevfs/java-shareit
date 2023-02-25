package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    @NotBlank
    private String name;
    @Email(message = "Email не корректен")
    @NotNull(message = "Email не может быть пустым")
    private String email;

}
