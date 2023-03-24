package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingDto {

    int id;
    String name;
    String description;
    Boolean available;
    ItemBookingDto lastBooking;
    ItemBookingDto nextBooking;
    List<CommentDto> comments;
}
