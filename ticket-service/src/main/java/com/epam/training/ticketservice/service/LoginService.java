package com.epam.training.ticketservice.service;

import org.springframework.stereotype.Service;

public interface LoginService {

    void signInPrivileged(String username, String password);
    void signIn(String username, String password);
    void signOut();
}
