package com.epam.training.ticketservice.core.booking.model;


import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class TicketDto {

   private final List<SeatDto> seats;
   private final ScreeningDto screening;
   private final String username;
   private final Integer price;

   public List<SeatDto> getSeats() {
      return Collections.unmodifiableList(seats);
   }
}
