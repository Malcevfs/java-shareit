package ru.practicum.shareit.item.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    int id;
    String authorName;
    @NotBlank
    String text;
    LocalDateTime created;
}
