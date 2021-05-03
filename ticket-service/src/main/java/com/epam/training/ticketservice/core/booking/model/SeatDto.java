package com.epam.training.ticketservice.core.booking.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

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
    @Generated
    public String toString() {
        return String.format("(%s,%s)", row, column);
    }

    public static String seatsToString(Collection<SeatDto> seats) {
        StringBuilder builder = new StringBuilder();
        int seatsSize = seats.size();
        for (SeatDto seatDto : seats) {
            seatsSize--;
            builder.append(seatDto);
            if (seatsSize > 0) {
                builder.append(", ");

            }
        }
        return builder.toString();
    }
}
