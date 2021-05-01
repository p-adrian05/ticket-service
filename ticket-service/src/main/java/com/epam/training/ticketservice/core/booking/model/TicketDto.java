package com.epam.training.ticketservice.core.booking.model;

import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;


@EqualsAndHashCode
@Builder
@Getter
public class TicketDto {

    private final Set<SeatDto> seats;
    private final BasicScreeningDto screening;
    private final String username;
    private final Money price;

    public Set<SeatDto> getSeats() {
        return Collections.unmodifiableSet(seats);
    }

    @Override
    public String toString() {
        return String.format("Seats %s, on %s in room %s starting at %s for %s HUF ",seats,screening.getMovieName(),screening.getRoomName(), screening.getTime().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),price);
    }
}
