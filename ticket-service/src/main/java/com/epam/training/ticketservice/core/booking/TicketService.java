package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.TicketCreateException;
import com.epam.training.ticketservice.core.booking.model.TicketDto;

import java.util.Optional;

public interface TicketService {

    Optional<Integer> book(TicketDto ticket) throws TicketCreateException;

    Optional<Integer> showPrice(TicketDto ticket);
}

