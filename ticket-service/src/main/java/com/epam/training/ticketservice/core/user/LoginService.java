package com.epam.training.ticketservice.core.user;

import com.epam.training.ticketservice.core.user.model.UserDto;

import java.util.Optional;

public interface LoginService {

    Optional<UserDto> logOut();

    Optional<UserDto> login(String username, String password);

    Optional<UserDto> loginAsPrivileged(String username, String password);

    Optional<UserDto> getLoggedInUser();
}
