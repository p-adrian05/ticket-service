package com.epam.training.ticketservice.persistence.dao;

import com.epam.training.ticketservice.persistence.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.RoomAlreadyExistsException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.persistence.exceptions.UnknownRoomException;

import java.util.Collection;

public interface CrudDao<T> {

    void create(T object) throws MovieAlreadyExistsException, RoomAlreadyExistsException;

    void update(T object) throws UnknownMovieException, UnknownRoomException;

    void delete(T object) throws UnknownMovieException, UnknownRoomException;

    Collection<T> readAll();
}
