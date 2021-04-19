package com.epam.training.ticketservice.service.impl;

import com.epam.training.ticketservice.persistence.dao.AccountDao;
import com.epam.training.ticketservice.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private AccountDao accountDao;


    @Override
    public void signInPrivileged(String username, String password) {

    }

    @Override
    public void signIn(String username, String password) {

    }

    @Override
    public void signOut() {

    }
}
