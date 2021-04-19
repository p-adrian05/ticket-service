package com.epam.training.ticketservice.persistence.dao;

import com.epam.training.ticketservice.persistence.exceptions.UnknownMovieException;

import java.util.Collection;

public interface CrudDao<T> {

    int create(T object);

    void update(T object) throws UnknownMovieException;

    void delete(T object) throws UnknownMovieException;

    Collection<T> readAll();
}
