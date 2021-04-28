package com.epam.training.ticketservice.core.booking.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.logging.log4j.message.StringFormattedMessage;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class SeatDto {

    private final Integer row;
    private final Integer column;

    public static SeatDto of(int row, int column) {
        return new SeatDto(row, column);
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)",row,column);
    }
}
