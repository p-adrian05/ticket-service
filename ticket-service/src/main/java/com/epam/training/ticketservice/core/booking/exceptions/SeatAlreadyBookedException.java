package com.epam.training.ticketservice.core.booking.exceptions;

public class SeatAlreadyBookedException extends TicketCreateException{

    public SeatAlreadyBookedException() {
        super();
    }

    public SeatAlreadyBookedException(String message) {
        super(message);
    }

}
