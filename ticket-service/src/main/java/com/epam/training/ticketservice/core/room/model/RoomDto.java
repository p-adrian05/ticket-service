package com.epam.training.ticketservice.core.room.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class RoomDto {

    private final String name;

    private final Integer rows;

    private final Integer columns;

    @Override
    public String toString() {
        return String.format("Room %s with %s seats, %s rows and %s columns", name, rows * columns, rows, columns);
    }
}
