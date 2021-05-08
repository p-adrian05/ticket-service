package com.epam.training.ticketservice.core.price;

import com.epam.training.ticketservice.core.price.exceptions.AttachPriceException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;

public interface AttachPriceService {

    void attachMovie(String movieName, String priceName) throws AttachPriceException, UnknownPriceException;

    void attachRoom(String roomName, String priceName) throws AttachPriceException, UnknownPriceException;

    void attachScreening(BasicScreeningDto basicScreeningDto, String priceName)
        throws AttachPriceException, UnknownPriceException;
}
