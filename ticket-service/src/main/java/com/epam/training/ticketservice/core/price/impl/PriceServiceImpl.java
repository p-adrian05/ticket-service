package com.epam.training.ticketservice.core.price.impl;

import com.epam.training.ticketservice.core.price.PriceService;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PriceServiceImpl implements PriceService {

    @Override
    public void createPrice(PriceDto priceDto){

    }

    @Override
    public void updatePrice(String name, int value) {

    }
}
