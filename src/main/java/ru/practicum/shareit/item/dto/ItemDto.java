package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
