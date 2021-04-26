package com.epam.training.ticketservice.core.account;

import com.epam.training.ticketservice.core.account.exception.UsernameAlreadyExistsException;
import com.epam.training.ticketservice.core.account.model.UserDto;

import java.util.Optional;

public interface AccountService {

    Optional<UserDto> getUserByName(String username);

    void signOut();

    void signUp(String username, String password) throws UsernameAlreadyExistsException;

    void signIn(String username,String password);

    UserDto getSignedInUser();

}
