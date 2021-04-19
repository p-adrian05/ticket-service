package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.persistence.AccountRepository;
import com.epam.training.ticketservice.persistence.dao.AccountDao;
import com.epam.training.ticketservice.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Repository
public class AccountDaoImpl implements AccountDao {


   private AccountRepository accountRepository;

    @Override
    public Optional<User> getUserByName(String username) {
        return Optional.empty();
    }

    @Override
    public void  create(User object) {
    }

    @Override
    public void update(User object) {

    }

    @Override
    public void delete(User object) {

    }

    @Override
    public Collection<User> readAll() {
        return null;
    }
}
