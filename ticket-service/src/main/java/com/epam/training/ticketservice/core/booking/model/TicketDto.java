package com.epam.training.ticketservice.core.booking.model;


import com.epam.training.ticketservice.core.screening.model.CreateScreeningDto;
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
public class TicketDto {

    private final Set<SeatDto> seats;
    private final CreateScreeningDto screening;
    private final String username;
    private final Integer price;

    public Set<SeatDto> getSeats() {
        return Collections.unmodifiableSet(seats);
    }
}
