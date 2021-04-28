package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;

import java.util.Set;

public interface SeatService {

    boolean isFreeToSeat(Set<SeatDto> seats, ScreeningEntity screeningEntity)  throws BookingException;
    void bookSeatsToTicket(Set<SeatDto> seats,TicketEntity ticketEntity) throws BookingException;
    int calculateSeatPrice(Set<SeatDto> seats);
}
