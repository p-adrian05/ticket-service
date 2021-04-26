package com.epam.training.ticketservice.core.booking.exceptions;

public class UndefinedSeatPriceException extends TicketCreateException {

    public UndefinedSeatPriceException() {
        super();
    }

    public UndefinedSeatPriceException(String message) {
        super(message);
    }

}
