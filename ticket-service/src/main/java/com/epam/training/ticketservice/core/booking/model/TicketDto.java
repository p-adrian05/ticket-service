package com.epam.training.ticketservice.core.booking.model;


import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import lombok.*;

import java.util.Collections;
import java.util.Set;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class TicketDto {

   private final Set<SeatDto> seats;
   private final ScreeningDto screening;
   private final String username;
   private final Integer price;

   public Set<SeatDto> getSeats() {
      return Collections.unmodifiableSet(seats);
   }
}
