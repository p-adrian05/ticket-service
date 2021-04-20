package com.epam.training.ticketservice.core.movie.exceptions;

public class UnknownMovieException extends Exception{

    public UnknownMovieException() {
        super();
    }

    public UnknownMovieException(String message) {
        super(message);
    }

}
