package com.epam.training.ticketservice.core.price.exceptions;

public class UnknownPriceException extends Exception {

    public UnknownPriceException() {
        super();
    }

    public UnknownPriceException(String message) {
        super(message);
    }

}
