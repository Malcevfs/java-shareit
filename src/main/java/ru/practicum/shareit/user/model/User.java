package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class User {
    private int id;
    @NotBlank
    private String name;
    @Email(message = "Email не корректен")
    @NotNull(message = "Email не может быть пустым")
    private String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}

