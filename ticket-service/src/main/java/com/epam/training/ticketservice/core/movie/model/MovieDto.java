package com.epam.training.ticketservice.core.movie.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class MovieDto {

    private final String title;

    private final String genre;

    private final Integer duration;

    @Override
    @Generated
    public String toString() {
        return String.format("%s (%s, %s minutes)",title,genre,duration);
    }
}
