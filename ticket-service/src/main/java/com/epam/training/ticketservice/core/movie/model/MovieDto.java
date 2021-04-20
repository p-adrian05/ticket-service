package com.epam.training.ticketservice.core.movie.model;

import lombok.*;

@EqualsAndHashCode
@Getter
@ToString
@Builder
@RequiredArgsConstructor
public class MovieDto {

    private final String title;

    private final String genre;

    private final Integer duration;

}
