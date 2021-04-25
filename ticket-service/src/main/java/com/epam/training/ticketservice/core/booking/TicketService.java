package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.TicketCreateException;
import com.epam.training.ticketservice.core.booking.model.TicketDto;

public interface TicketService {

    int book(TicketDto ticket) throws TicketCreateException;
}

