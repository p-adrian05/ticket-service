package com.epam.training.ticketservice.persistence.dao;

import com.epam.training.ticketservice.model.User;

import java.util.Optional;

public interface AccountDao extends CrudDao<User> {

    Optional<User> getUserByName(String username);

}
