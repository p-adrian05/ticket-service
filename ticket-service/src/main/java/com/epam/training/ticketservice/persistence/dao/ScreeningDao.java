package com.epam.training.ticketservice.persistence.dao;

import com.epam.training.ticketservice.model.Movie;
import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.persistence.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownRoomException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownScreeningException;

import java.util.Collection;

public interface ScreeningDao{


    void createScreening(Screening screening) throws UnknownMovieException, UnknownRoomException;

    void deleteScreening(Screening screening) throws UnknownScreeningException;

    Collection<Screening> readAllScreenings();

}
