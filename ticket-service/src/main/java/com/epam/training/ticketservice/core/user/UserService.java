package com.epam.training.ticketservice.core.user;

import com.epam.training.ticketservice.core.user.exception.UserAlreadyExistsException;
import com.epam.training.ticketservice.core.user.model.RegistrationDto;
import com.epam.training.ticketservice.core.user.model.UserDto;

import java.util.Optional;

public interface UserService {

    Optional<UserDto> getUserByNameAndPassword(String username,String password);

    Optional<UserDto> getPrivilegedUserByNameAndPassword(String username,String password);

    void registerUser(RegistrationDto registrationDto) throws UserAlreadyExistsException;

}
