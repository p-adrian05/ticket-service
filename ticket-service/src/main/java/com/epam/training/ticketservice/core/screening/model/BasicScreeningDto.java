package com.epam.training.ticketservice.core.screening.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class BasicScreeningDto {

    private final String movieName;

    private final String roomName;

    private final LocalDateTime time;

    @Override
    @Generated
    public String toString() {
        return String.format("%s, screened in room %s, at %s", movieName, roomName,
            time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
}
