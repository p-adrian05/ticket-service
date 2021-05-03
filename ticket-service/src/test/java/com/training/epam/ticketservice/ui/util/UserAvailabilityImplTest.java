package com.training.epam.ticketservice.ui.util;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.ui.util.UserAvailabilityImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.shell.Availability;

import java.util.List;
import java.util.Optional;

public class UserAvailabilityImplTest {


    private UserAvailabilityImpl underTest;
    private LoginService loginService;

    private final static UserDto ADMIN_USER_DTO = UserDto.builder()
        .username("admin")
        .role(UserEntity.Role.ADMIN)
        .build();
    private final static UserDto USER_USER_DTO = UserDto.builder()
        .username("user")
        .role(UserEntity.Role.USER)
        .build();

    @BeforeEach
    public void init() {
        loginService = Mockito.mock(LoginService.class);
        underTest = new UserAvailabilityImpl(loginService);
    }

    @Test
    public void testIsAdminAvailableShouldCallLoginServiceAndReturnIsAvailableWhenAdminUserIsLoggedIn() {
        // Given
        Mockito.when(loginService.getLoggedInUser()).thenReturn(Optional.of(ADMIN_USER_DTO));

        // When
        Availability actual = underTest.isAdminAvailable();

        // Then
        Assertions.assertTrue(actual.isAvailable());
        Mockito.verify(loginService).getLoggedInUser();
        Mockito.verifyNoMoreInteractions(loginService);
    }

    @Test
    public void testIsAdminAvailableShouldCallLoginServiceAndReturnIsUnAvailableWhenNoUserIsLoggedIn() {
        // Given
        Mockito.when(loginService.getLoggedInUser()).thenReturn(Optional.empty());

        // When
        Availability actual = underTest.isAdminAvailable();

        // Then
        Assertions.assertFalse(actual.isAvailable());
        Mockito.verify(loginService).getLoggedInUser();
        Mockito.verifyNoMoreInteractions(loginService);
    }

    @Test
    public void testIsAdminAvailableShouldCallLoginServiceAndReturnIsUnAvailableWhenNotAdminUserIsLoggedIn() {
        // Given
        Mockito.when(loginService.getLoggedInUser()).thenReturn(Optional.of(USER_USER_DTO));

        // When
        Availability actual = underTest.isAdminAvailable();

        // Then
        Assertions.assertFalse(actual.isAvailable());
        Mockito.verify(loginService).getLoggedInUser();
        Mockito.verifyNoMoreInteractions(loginService);
    }

    @Test
    public void testIsUserAvailableShouldCallLoginServiceAndReturnIsAvailableWhenUserIsLoggedIn() {
        // Given
        Mockito.when(loginService.getLoggedInUser()).thenReturn(Optional.of(USER_USER_DTO));

        // When
        Availability actual = underTest.isUserAvailable();

        // Then
        Assertions.assertTrue(actual.isAvailable());
        Mockito.verify(loginService).getLoggedInUser();
        Mockito.verifyNoMoreInteractions(loginService);
    }

    @Test
    public void testIsUserAvailableShouldCallLoginServiceAndReturnIsUnAvailableWhenNoUserIsLoggedIn() {
        // Given
        Mockito.when(loginService.getLoggedInUser()).thenReturn(Optional.empty());

        // When
        Availability actual = underTest.isUserAvailable();

        // Then
        Assertions.assertFalse(actual.isAvailable());
        Mockito.verify(loginService).getLoggedInUser();
        Mockito.verifyNoMoreInteractions(loginService);
    }

    @Test
    public void testIsAdminAvailableShouldCallLoginServiceAndReturnIsUnAvailableWhenNotUserIsLoggedIn() {
        // Given
        Mockito.when(loginService.getLoggedInUser()).thenReturn(Optional.of(ADMIN_USER_DTO));

        // When
        Availability actual = underTest.isUserAvailable();

        // Then
        Assertions.assertFalse(actual.isAvailable());
        Mockito.verify(loginService).getLoggedInUser();
        Mockito.verifyNoMoreInteractions(loginService);
    }
}
