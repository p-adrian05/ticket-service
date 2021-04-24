package com.epam.training.ticketservice.core.room.model;

import lombok.*;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class RoomDto {

    private final String name;

    private final Integer rows;

    private final Integer columns;

}
