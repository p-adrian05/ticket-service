package com.epam.training.ticketservice.persistence.exceptions;

public class RoomAlreadyExistsException extends Exception{

    public RoomAlreadyExistsException() {
        super();
    }

    public RoomAlreadyExistsException(String message) {
        super(message);
    }

}
