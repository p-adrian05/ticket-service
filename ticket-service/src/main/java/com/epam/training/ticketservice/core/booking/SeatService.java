package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.persistence.entity.SeatEntity;
import com.epam.training.ticketservice.core.booking.persistence.entity.TicketEntity;
import com.epam.training.ticketservice.core.finance.money.Money;
import com.epam.training.ticketservice.core.screening.persistence.entity.ScreeningEntity;

import java.util.Optional;
import java.util.Set;

public interface SeatService {

    boolean isFreeToSeat(Set<SeatDto> seats, ScreeningEntity screeningEntity) throws BookingException;

    public Optional<Money> bookSeatsToTicket(Set<SeatDto> seats, TicketEntity ticketEntity,
                                             ScreeningEntity screeningEntity) throws BookingException;

    Money calculateSeatPrice(Set<SeatDto> seats);
}
