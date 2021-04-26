package com.epam.training.ticketservice.core.account.exception;

public class UsernameAlreadyExistsException extends Exception {

    public UsernameAlreadyExistsException() {
        super();
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }

}
