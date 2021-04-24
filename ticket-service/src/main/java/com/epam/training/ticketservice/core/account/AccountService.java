package com.epam.training.ticketservice.core.account;

import com.epam.training.ticketservice.core.account.model.UserDto;

import java.util.Optional;

public interface AccountService {

    Optional<UserDto> getUserByName(String username);

    void createAccount(UserDto userDto);

}
