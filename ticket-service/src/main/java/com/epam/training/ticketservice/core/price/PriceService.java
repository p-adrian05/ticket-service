package com.epam.training.ticketservice.core.price;

import com.epam.training.ticketservice.core.price.exceptions.AttachPriceException;
import com.epam.training.ticketservice.core.price.exceptions.PriceAlreadyExistsException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;


public interface PriceService {

    void createPrice(PriceDto priceDto) throws PriceAlreadyExistsException;

    void updatePrice(PriceDto priceDto) throws UnknownPriceException;

    void attachMovie(String movieName,String priceName) throws AttachPriceException, UnknownPriceException;

    void attachRoom(String roomName,String priceName) throws AttachPriceException, UnknownPriceException;

    void attachScreening(ScreeningDto screeningDto, String priceName) throws AttachPriceException, UnknownPriceException;
}
