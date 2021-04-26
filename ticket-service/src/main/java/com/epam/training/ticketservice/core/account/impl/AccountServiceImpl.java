package com.epam.training.ticketservice.core.account.impl;

import com.epam.training.ticketservice.core.account.AccountService;
import com.epam.training.ticketservice.core.account.exception.UsernameAlreadyExistsException;
import com.epam.training.ticketservice.core.account.model.UserDto;
import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import com.epam.training.ticketservice.core.account.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private UserDto signedInUser = null;

    @PostConstruct
    private void init() throws UsernameAlreadyExistsException {
        signUpWithPrivileged("admin", "admin");
    }

    @Override
    public Optional<UserDto> getUserByName(String username) {
        return convertEntityToDto(accountRepository.findByUsername(username));
    }


    @Override
    public void signIn(String username, String password) {
        Objects.requireNonNull(password, "User password cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        Optional<AccountEntity> accountEntity = accountRepository.findByUsername(username);

        if (accountEntity.isPresent() && passwordEncoder.matches(password, (accountEntity.get().getPassword()))) {
            this.signedInUser = UserDto.builder().isPrivileged(accountEntity.get().isPrivileged())
                .username(username).build();
        } else {
            throw new BadCredentialsException("Wrong username or password");
        }
    }

    @Override
    @Transactional
    public void signUp(String username, String password) throws UsernameAlreadyExistsException {
        signUp(username, password, false);
    }

    private void signUp(String username, String password, boolean isPrivileged) throws UsernameAlreadyExistsException {
        Objects.requireNonNull(password, "User password cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        if (accountRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(
                String.format("Failed to create user, username already exists: %s ", username));
        }
        AccountEntity accountEntity = AccountEntity.builder()
            .isPrivileged(isPrivileged)
            .username(username)
            .password(passwordEncoder.encode(password))
            .build();
        accountRepository.save(accountEntity);
    }

    @Override
    public void signOut() {
        this.signedInUser = null;
    }

    public UserDto getSignedInUser() {
        return signedInUser;
    }

    private void signUpWithPrivileged(String username, String password) throws UsernameAlreadyExistsException {
        signUp(username, password, true);
    }

    private Optional<UserDto> convertEntityToDto(Optional<AccountEntity> accountEntity) {
        return accountEntity.map(this::convertEntityToDto);
    }

    private UserDto convertEntityToDto(AccountEntity accountEntity) {
        return UserDto.builder()
            .isPrivileged(accountEntity.isPrivileged())
            .username(accountEntity.getUsername())
            .build();
    }

}
