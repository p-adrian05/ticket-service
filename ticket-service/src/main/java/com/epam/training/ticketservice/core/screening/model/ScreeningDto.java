package com.epam.training.ticketservice.core.screening.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningDto {

    private String movieName;

    private String roomName;

    private LocalDateTime time;

}
