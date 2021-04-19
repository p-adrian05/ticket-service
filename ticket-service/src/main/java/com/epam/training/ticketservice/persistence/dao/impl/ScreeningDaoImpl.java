package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.persistence.AccountRepository;
import com.epam.training.ticketservice.persistence.dao.ScreeningDao;
import com.epam.training.ticketservice.model.Screening;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Repository
public class ScreeningDaoImpl implements ScreeningDao {


   private AccountRepository accountRepository;

    @Override
    public int create(Screening screening) {
        return 0;
    }

    @Override
    public void update(Screening screening) {

    }

    @Override
    public void delete(Screening screening) {

    }

    @Override
    public Collection<Screening> readAll() {
        return null;
    }

}
