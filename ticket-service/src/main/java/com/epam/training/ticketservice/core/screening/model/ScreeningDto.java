package com.epam.training.ticketservice.core.screening.model;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class ScreeningDto {

    private final MovieDto movieDto;

    private final String roomName;

    private final LocalDateTime time;

    @Override
    public String toString() {
        return String.format("%s, screened in room %s, at %s",movieDto.toString(),roomName,
                time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
}
