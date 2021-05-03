package com.training.epam.ticketservice.core.account.impl;

import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.exception.UserAlreadyExistsException;
import com.epam.training.ticketservice.core.user.impl.LoginServiceImpl;
import com.epam.training.ticketservice.core.user.impl.UserServiceImpl;
import com.epam.training.ticketservice.core.user.model.RegistrationDto;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class LoginServiceImplTest {

    private UserServiceImpl userService;
    private LoginServiceImpl underTest;

    private final static String USERNAME = "admin";
    private final static String PASSWORD = "password";

    @BeforeEach
    public void init() {
        userService = Mockito.mock(UserServiceImpl.class);
        underTest = new LoginServiceImpl(userService);
    }
    private final static UserDto USER_DTO = UserDto.builder()
        .username("admin")
        .role(UserEntity.Role.ADMIN)
        .build();
    @Test
    public void testLoginShouldCallUserService() {
        // Given
        Mockito.when(userService.getUserByNameAndPassword(USERNAME,PASSWORD)).thenReturn(Optional.of(
            USER_DTO));
        // When
        underTest.login(USERNAME, PASSWORD);
        // Then
        Mockito.verify(userService).getUserByNameAndPassword(USERNAME,PASSWORD);
        Mockito.verifyNoMoreInteractions(userService);

    }
    @Test
    public void testLoginAsPrivilegedShouldCallUserService() {
        // Given
        Mockito.when(userService.getPrivilegedUserByNameAndPassword(USERNAME,PASSWORD)).thenReturn(Optional.of(
            USER_DTO));
        // When
        underTest.loginAsPrivileged(USERNAME, PASSWORD);
        // Then
        Mockito.verify(userService).getPrivilegedUserByNameAndPassword(USERNAME,PASSWORD);
        Mockito.verifyNoMoreInteractions(userService);

    }

}
