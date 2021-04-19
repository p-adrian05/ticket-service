package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.model.Screening;
import com.epam.training.ticketservice.model.Ticket;
import com.epam.training.ticketservice.persistence.AccountRepository;
import com.epam.training.ticketservice.persistence.dao.ScreeningDao;
import com.epam.training.ticketservice.persistence.dao.TicketDao;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Repository
public class TicketDaoImpl implements TicketDao {


    @Override
    public int create(Ticket ticket) {
        return 0;
    }

    @Override
    public void update(Ticket ticket) {

    }

    @Override
    public void delete(Ticket ticket) {

    }

    @Override
    public Collection<Ticket> readAll() {
        return null;
    }
}
