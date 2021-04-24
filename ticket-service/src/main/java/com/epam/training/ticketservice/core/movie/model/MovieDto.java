package com.epam.training.ticketservice.core.movie.model;

import lombok.*;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class MovieDto {

    private final String title;

    private final String genre;

    private final Integer duration;

}
