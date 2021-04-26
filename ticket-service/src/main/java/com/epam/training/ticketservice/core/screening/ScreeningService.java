package com.epam.training.ticketservice.core.screening;

import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.model.CreateScreeningDto;
import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;

import java.util.List;

public interface ScreeningService {


    void createScreening(CreateScreeningDto createScreeningDto) throws ScreeningCreationException;

    void deleteScreening(CreateScreeningDto createScreeningDto) throws UnknownScreeningException;

    List<ScreeningDto> getScreenings();

}
