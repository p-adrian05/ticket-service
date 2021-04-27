package com.epam.training.ticketservice.core.user.impl;

import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private UserDto loggedInUser = null;

    @Override
    public Optional<UserDto> logOut() {
        Optional<UserDto> previouslyLoggedInUser = getLoggedInUser();
        loggedInUser = null;
        System.out.println(getLoggedInUser());
        return previouslyLoggedInUser;
    }

    @Override
    public Optional<UserDto> login(String username, String password) {
        Objects.requireNonNull(username, "Username can not be null for login");
        Objects.requireNonNull(username, "Password can not be null for login");
        loggedInUser = userService.getUserByNameAndPassword(username, password).orElse(null);
        return getLoggedInUser();
    }

    @Override
    public Optional<UserDto> loginAsPrivileged(String username, String password) {
        Objects.requireNonNull(username, "Username can not be null for login");
        Objects.requireNonNull(username, "Password can not be null for login");
        loggedInUser = userService.getPrivilegedUserByNameAndPassword(username, password).orElse(null);
        return getLoggedInUser();
    }

    @Override
    public Optional<UserDto> getLoggedInUser() {
        return Optional.ofNullable(loggedInUser);
    }
}
