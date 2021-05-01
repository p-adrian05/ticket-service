package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.finance.money.Money;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    TicketDto book(BookingDto bookingDto, String username, String currency) throws BookingException;

    Optional<Money> showPrice(BookingDto bookingDto, String currency) throws BookingException;

    List<TicketDto> getTicketsByUsername(String username);
}

