package com.epam.training.ticketservice.core.movie.exceptions;

public class MovieAlreadyExistsException extends Exception {

    public MovieAlreadyExistsException(String message) {
        super(message);
    }

}
