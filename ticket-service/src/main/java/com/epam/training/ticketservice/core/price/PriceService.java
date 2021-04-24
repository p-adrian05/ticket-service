package com.epam.training.ticketservice.core.price;

import com.epam.training.ticketservice.core.price.exceptions.PriceAlreadyExistsException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.model.PriceDto;


public interface PriceService {

    void createPrice(PriceDto priceDto) throws PriceAlreadyExistsException;

    void updatePrice(PriceDto priceDto) throws UnknownPriceException;
}
