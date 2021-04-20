package com.epam.training.ticketservice.core.account.impl;

import com.epam.training.ticketservice.core.account.AccountService;
import com.epam.training.ticketservice.core.account.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {


        @Override
        public Optional<User> getUserByName(String username) {
            return Optional.empty();
        }

}
