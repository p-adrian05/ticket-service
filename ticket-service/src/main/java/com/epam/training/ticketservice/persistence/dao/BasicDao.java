package com.epam.training.ticketservice.persistence.dao;

import java.util.Collection;

public interface BasicDao<T> {

    int create(T object);

    void update(T object);

    void delete(T object);

    Collection<T> readAll();
}
