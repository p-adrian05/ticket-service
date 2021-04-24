package com.epam.training.ticketservice.core.booking;

import com.epam.training.ticketservice.core.booking.exceptions.TicketCreateException;
import com.epam.training.ticketservice.core.booking.model.TicketDto;

public interface TicketService {

    void createTicket(TicketDto ticket) throws TicketCreateException;
}

