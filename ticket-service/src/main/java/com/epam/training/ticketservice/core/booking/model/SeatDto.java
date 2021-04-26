package com.epam.training.ticketservice.core.booking.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class SeatDto {

    private final Integer row;
    private final Integer column;

    public static SeatDto of(int row, int column) {
        return new SeatDto(row, column);
    }
}
