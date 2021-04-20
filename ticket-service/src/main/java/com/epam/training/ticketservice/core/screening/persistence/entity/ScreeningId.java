package com.epam.training.ticketservice.core.screening.persistence.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningId implements Serializable {


    private int roomId;

    private int movieId;

    private LocalDateTime time;

}
