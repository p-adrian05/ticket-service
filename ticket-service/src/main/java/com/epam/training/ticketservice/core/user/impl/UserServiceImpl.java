package com.epam.training.ticketservice.core.user.impl;

import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.exception.UserAlreadyExistsException;
import com.epam.training.ticketservice.core.user.model.RegistrationDto;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<UserDto> getUserByNameAndPassword(String username, String password) {
        Objects.requireNonNull(username, "Username can not be null");
        return matchUserPassword(password, () -> userRepository.findByUsername(username));
    }

    @Override
    public Optional<UserDto> getPrivilegedUserByNameAndPassword(String username, String password) {
        Objects.requireNonNull(username, "Username can not be null");
        return matchUserPassword(password, () -> userRepository.findByUsernameAndRole(username,UserEntity.Role.ADMIN));
    }

    private Optional<UserDto> matchUserPassword(String password, Supplier<Optional<UserEntity>> entitySupplier) {
        Objects.requireNonNull(password, "Password can not be null");
        Optional<UserEntity> userEntity = entitySupplier.get();
        if (userEntity.isPresent()) {
            if (passwordEncoder.matches(password, userEntity.get().getPassword())) {
                return convertEntityToDto(userEntity);
            }
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void registerUser(RegistrationDto registrationDto) throws UserAlreadyExistsException {
        Objects.requireNonNull(registrationDto.getPassword(), "User password cannot be null");
        Objects.requireNonNull(registrationDto.getUsername(), "Username cannot be null");
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException(
                String.format("Failed to create user, username already exists: %s ", registrationDto.getUsername()));
        }
        UserEntity userEntity = UserEntity.builder()
            .role(UserEntity.Role.USER)
            .username(registrationDto.getUsername())
            .password(passwordEncoder.encode(registrationDto.getPassword()))
            .build();
        userRepository.save(userEntity);
    }

    private Optional<UserDto> convertEntityToDto(Optional<UserEntity> accountEntity) {
        return accountEntity.map(this::convertEntityToDto);
    }

    private UserDto convertEntityToDto(UserEntity userEntity) {
        return UserDto.builder()
            .role(userEntity.getRole())
            .username(userEntity.getUsername())
            .build();
    }
}
