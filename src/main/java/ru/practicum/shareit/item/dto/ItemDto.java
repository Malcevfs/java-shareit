package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    @NotEmpty(message = "Название для предмета не может быть пустым")
    String name;
    @NotBlank(message = "Описание для предмета не может быть пустым")
    String description;
    Boolean available;

}
