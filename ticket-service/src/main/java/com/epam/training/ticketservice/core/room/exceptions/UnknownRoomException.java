package com.epam.training.ticketservice.core.room.exceptions;

public class UnknownRoomException extends Exception{

    public UnknownRoomException() {
        super();
    }

    public UnknownRoomException(String message) {
        super(message);
    }

}
