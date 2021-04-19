package com.epam.training.ticketservice.persistence.dao;

import com.epam.training.ticketservice.model.Movie;

public interface MovieDao extends CrudDao<Movie> {

    void deleteByTitle(String title);
}

