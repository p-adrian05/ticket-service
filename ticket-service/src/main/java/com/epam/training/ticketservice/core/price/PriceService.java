package com.epam.training.ticketservice.core.price;

import com.epam.training.ticketservice.core.price.model.PriceDto;


public interface PriceService {

    void createPrice(PriceDto priceDto);

    void updatePrice(String name, int value);
}
