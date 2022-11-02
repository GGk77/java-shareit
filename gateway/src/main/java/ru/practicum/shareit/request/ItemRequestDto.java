package ru.practicum.shareit.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Integer id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemDto {
        private Integer id;

        private String name;

        private String description;

        private Boolean available;

        private Integer requestId;
    }

}