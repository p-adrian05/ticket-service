package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.booking.model.SeatDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
class SeatsConverter implements Converter<String, Set<SeatDto>> {

    @Override
    public Set<SeatDto> convert(String source) {
        String[] seats = source.split(" ");
        String[] seatArray;
        Set<SeatDto> result = new HashSet<>();
        for (String seat : seats) {
            seatArray = seat.split(",");
            result.add(SeatDto.of(Integer.parseInt(seatArray[0]), Integer.parseInt(seatArray[1])));
        }
        return Collections.unmodifiableSet(result);
    }
}