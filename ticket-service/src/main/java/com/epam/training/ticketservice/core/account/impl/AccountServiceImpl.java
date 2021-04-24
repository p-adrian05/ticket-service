package com.epam.training.ticketservice.core.account.impl;

import com.epam.training.ticketservice.core.account.AccountService;
import com.epam.training.ticketservice.core.account.model.UserDto;
import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import com.epam.training.ticketservice.core.account.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;


        @Override
        public Optional<UserDto> getUserByName(String username) {
            return Optional.empty();
        }

    @Override
    public void createAccount(UserDto userDto) {
        AccountEntity accountEntity = AccountEntity.builder()
                .isPrivileged(userDto.isPrivileged())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .build();
        accountRepository.save(accountEntity);
    }

}
