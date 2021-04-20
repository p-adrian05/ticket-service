package com.epam.training.ticketservice.core.account;

import com.epam.training.ticketservice.core.account.model.User;

import java.util.Optional;

public interface AccountService {

    Optional<User> getUserByName(String username);

}
