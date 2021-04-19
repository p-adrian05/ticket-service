package com.epam.training.ticketservice.persistence.dao.impl;

import com.epam.training.ticketservice.model.Price;
import com.epam.training.ticketservice.persistence.dao.PriceDao;
import com.epam.training.ticketservice.persistence.dao.TicketDao;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Repository
public class PriceDaoImpl implements PriceDao {


    @Override
    public void create(Price price) {

    }

    @Override
    public void update(Price price) {

    }

    @Override
    public void delete(Price price) {

    }

    @Override
    public Collection<Price> readAll() {
        return null;
    }
}
