package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "Название для предмета не может быть пустым")
    private String name;
    @NotBlank(message = "Описание для предмета не может быть пустым")
    private String description;
//    @NotBlank(message = "Параметр available не может быть пустым ")
    @Column(name = "is_available")
    private Boolean available;
    @Column(name = "owner_id")
    private int owner;
//    @Column(name = "request_id")
//    private ItemRequest request;

}
