package com.epam.training.ticketservice.core.screening;

import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;

import java.util.Collection;

public interface ScreeningService {


    void createScreening(ScreeningDto screeningDto) throws ScreeningCreationException;

    void deleteScreening(ScreeningDto screeningDto) throws UnknownScreeningException;

    Collection<ScreeningDto> readAllScreenings();

}
