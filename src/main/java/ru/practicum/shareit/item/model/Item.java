package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class Item {
    private int id;
    @NotEmpty(message = "Название для предмета не может быть пустым")
    private String name;
    @NotBlank(message = "Описание для предмета не может быть пустым")
    private String description;
    private Boolean available;
    private int owner;
    private ItemRequest request;

    public Item(int id, String name, String description, Boolean available, int owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
