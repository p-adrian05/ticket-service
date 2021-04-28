package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.BookingException;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    TicketDto book(BookingDto bookingDto, String username) throws BookingException;

    Optional<Integer> showPrice(BookingDto ticket) throws BookingException;

    List<TicketDto> getTicketsByUsername(String username);
}

