package com.epam.training.ticketservice.core.booking.model;


import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class BookingDto {

    private final Set<SeatDto> seats;
    private final BasicScreeningDto screening;

    public Set<SeatDto> getSeats() {
        return Collections.unmodifiableSet(seats);
    }
}
