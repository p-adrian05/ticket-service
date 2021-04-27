package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.model.SeatDto;

import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;

import java.util.Set;

public interface SeatService {

    boolean isFreeToSeat(Set<SeatDto> toBookSeats, BasicScreeningDto screeningDto);
    void bookSeatsToScreening(Set<SeatDto> toBookSeats, BasicScreeningDto screeningDto, TicketEntity ticketEntity);
    int calculateSeatPrice(Set<SeatDto> seats);
}
