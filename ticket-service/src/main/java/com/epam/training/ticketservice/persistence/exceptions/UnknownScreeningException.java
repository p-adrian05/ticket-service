package com.epam.training.ticketservice.persistence.exceptions;

public class UnknownScreeningException extends Exception{

    public UnknownScreeningException() {
        super();
    }

    public UnknownScreeningException(String message) {
        super(message);
    }

}
