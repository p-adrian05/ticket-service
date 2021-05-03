package com.epam.training.ticketservice.core.price.exceptions;

public class PriceAlreadyExistsException extends Exception {

    public PriceAlreadyExistsException(String message) {
        super(message);
    }

}
