package com.epam.training.ticketservice.core.screening;

import com.epam.training.ticketservice.core.screening.exceptions.ScreeningCreationException;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.screening.exceptions.UnknownScreeningException;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;

import java.util.List;

public interface ScreeningService {


    void createScreening(BasicScreeningDto basicScreeningDto) throws ScreeningCreationException;

    void deleteScreening(BasicScreeningDto basicScreeningDto) throws UnknownScreeningException;

    List<ScreeningDto> getScreenings();

}
