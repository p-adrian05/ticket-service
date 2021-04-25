package com.epam.training.ticketservice.core.account;

import com.epam.training.ticketservice.core.account.exception.UsernameAlreadyExistsException;
import com.epam.training.ticketservice.core.account.model.UserDto;

import java.util.Optional;

public interface AccountService {

    Optional<UserDto> getUserByName(String username);

    void logOut();

    void signUp(String username, String password) throws UsernameAlreadyExistsException;

    void signIn(String username,String password);

    void signInWithPrivileged(String username,String password) throws UsernameAlreadyExistsException;

    UserDto getLoggedInUser();

}
