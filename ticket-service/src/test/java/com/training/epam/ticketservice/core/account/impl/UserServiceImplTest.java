package com.training.epam.ticketservice.core.account.impl;

import com.epam.training.ticketservice.core.user.exception.UserAlreadyExistsException;
import com.epam.training.ticketservice.core.user.impl.UserServiceImpl;
import com.epam.training.ticketservice.core.user.model.RegistrationDto;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserServiceImplTest {

    private UserServiceImpl underTest;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private final static UserDto USER_DTO = UserDto.builder()
        .username("admin")
        .role(UserEntity.Role.ADMIN)
        .build();

    private final static UserEntity ADMIN_USER_ENTITY = UserEntity.builder()
        .username("admin")
        .role(UserEntity.Role.ADMIN)
        .password("admin")
        .build();
    private final static UserEntity USER_ENTITY = UserEntity.builder()
        .username("user")
        .role(UserEntity.Role.USER)
        .password("user")
        .build();
    private final static RegistrationDto REGISTRATION_DTO_USER = RegistrationDto.builder()
        .username("user")
        .password("user")
        .build();

    private final static String USERNAME = "admin";
    private final static String PASSWORD = "password";

    @BeforeEach
    public void init() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        underTest = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    public void testGetUserByNameAndPasswordShouldCallUserRepositoryAndReturnAnOptionalDtoWhenInputIsValid() {
        // Given
        Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(
            ADMIN_USER_ENTITY));
        Mockito.when(passwordEncoder.matches(PASSWORD, ADMIN_USER_ENTITY.getPassword()))
            .thenReturn(true);
        // When
        Optional<UserDto> actual = underTest.getUserByNameAndPassword(USERNAME,
            PASSWORD);

        // Then
        Assertions.assertEquals(Optional.of(USER_DTO), actual);
        Mockito.verify(userRepository).findByUsername(USERNAME);
        Mockito.verify(passwordEncoder).matches(PASSWORD, ADMIN_USER_ENTITY.getPassword());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(passwordEncoder);

    }

    @Test
    public void testGetUserByNameAndPasswordShouldCallUserRepositoryAndReturnAnEmptyOptionalWhenPasswordIsInValid() {
        // Given
        Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(
            ADMIN_USER_ENTITY));
        Mockito.when(passwordEncoder.matches(PASSWORD, ADMIN_USER_ENTITY.getPassword()))
            .thenReturn(false);
        // When
        Optional<UserDto> actual = underTest.getUserByNameAndPassword(USERNAME,
            PASSWORD);

        // Then
        Assertions.assertEquals(Optional.empty(), actual);
        Mockito.verify(userRepository).findByUsername(USERNAME);
        Mockito.verify(passwordEncoder).matches(PASSWORD, ADMIN_USER_ENTITY.getPassword());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    public void testGetUserByNameAndPasswordShouldThrowNullPointerExceptionWhenUsernameIsNull() {
        // Given
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.getUserByNameAndPassword(null,
            PASSWORD));

        // Then
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testGetUserByNameAndPasswordShouldThrowNullPointerExceptionWhenPasswordIsNull() {
        // Given
        // When
        Assertions.assertThrows(NullPointerException.class,
            () -> underTest.getUserByNameAndPassword(USERNAME,
                null));

        // Then
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testGetPrivilegedUserByNameAndPasswordShouldCallUserRepositoryAndReturnAnOptionalDtoWhenInputIsValid() {
        // Given
        Mockito.when(userRepository.findByUsernameAndRole(USERNAME, UserEntity.Role.ADMIN))
            .thenReturn(Optional.of(ADMIN_USER_ENTITY));
        Mockito.when(passwordEncoder.matches(PASSWORD, ADMIN_USER_ENTITY.getPassword()))
            .thenReturn(true);
        // When
        Optional<UserDto> actual = underTest.getPrivilegedUserByNameAndPassword(USERNAME,
            PASSWORD);

        // Then
        Assertions.assertEquals(Optional.of(USER_DTO), actual);
        Mockito.verify(userRepository).findByUsernameAndRole(USERNAME, UserEntity.Role.ADMIN);
        Mockito.verify(passwordEncoder).matches(PASSWORD, ADMIN_USER_ENTITY.getPassword());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    public void testGetPrivilegedUserByNameAndPasswordShouldCallUserRepositoryAndReturnAnEmptyOptionalWhenPasswordIsInValid() {
        // Given
        Mockito.when(userRepository.findByUsernameAndRole(USERNAME, UserEntity.Role.ADMIN))
            .thenReturn(Optional.of(ADMIN_USER_ENTITY));
        Mockito.when(passwordEncoder.matches(PASSWORD, ADMIN_USER_ENTITY.getPassword()))
            .thenReturn(false);
        // When
        Optional<UserDto> actual = underTest.getPrivilegedUserByNameAndPassword(USERNAME,
            PASSWORD);

        // Then
        Assertions.assertEquals(Optional.empty(), actual);
        Mockito.verify(userRepository).findByUsernameAndRole(USERNAME, UserEntity.Role.ADMIN);
        Mockito.verify(passwordEncoder).matches(PASSWORD, ADMIN_USER_ENTITY.getPassword());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    public void testGetPrivilegedUserByNameAndPasswordShouldThrowNullPointerExceptionWhenUsernameIsNull() {
        // Given
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.getPrivilegedUserByNameAndPassword(null,
            PASSWORD));

        // Then
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testRegisterUserShouldCallUserRepositoryWhenRegistrationInputIsValid()
        throws UserAlreadyExistsException {
        // Given
        Mockito.when(userRepository.existsByUsername(REGISTRATION_DTO_USER.getUsername())).thenReturn(false);
        Mockito.when(userRepository.save(USER_ENTITY)).thenReturn(USER_ENTITY);
        Mockito.when(passwordEncoder.encode(REGISTRATION_DTO_USER.getPassword()))
            .thenReturn(REGISTRATION_DTO_USER.getPassword());
        // When
        underTest.registerUser(REGISTRATION_DTO_USER);

        // Then
        Mockito.verify(userRepository).existsByUsername(REGISTRATION_DTO_USER.getUsername());
        Mockito.verify(userRepository).save(USER_ENTITY);
        Mockito.verifyNoMoreInteractions(userRepository);

    }

    @Test
    public void testRegisterUserShouldThrowUserAlreadyExistsExceptionWhenUsernameAlreadyExists() {
        // Given
        Mockito.when(userRepository.existsByUsername(REGISTRATION_DTO_USER.getUsername())).thenReturn(true);
        // When
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> underTest.registerUser(REGISTRATION_DTO_USER));

        // Then
        Mockito.verify(userRepository).existsByUsername(REGISTRATION_DTO_USER.getUsername());
        Mockito.verifyNoMoreInteractions(userRepository);

    }

    @Test
    public void testRegisterUserShouldThrowNullPointerExceptionWhenUsernameIsNull() {
        // Given
        RegistrationDto registrationDto = new RegistrationDto(null, "pass");
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.registerUser(registrationDto));

        // Then
        Mockito.verifyNoMoreInteractions(userRepository);

    }

    @Test
    public void testRegisterUserShouldThrowNullPointerExceptionWhenPasswordIsNull() {
        // Given
        RegistrationDto registrationDto = new RegistrationDto("username", null);
        // When
        Assertions.assertThrows(NullPointerException.class, () -> underTest.registerUser(registrationDto));

        // Then
        Mockito.verifyNoMoreInteractions(userRepository);

    }
}
