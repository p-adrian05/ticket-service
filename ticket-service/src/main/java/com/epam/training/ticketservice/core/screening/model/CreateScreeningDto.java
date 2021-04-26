package com.epam.training.ticketservice.core.screening.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class ScreeningDto {

    private final String movieName;

    private final String roomName;

    private final LocalDateTime time;

}
