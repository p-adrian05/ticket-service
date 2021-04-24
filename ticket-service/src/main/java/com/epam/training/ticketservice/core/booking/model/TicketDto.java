package com.epam.training.ticketservice.core.booking.model;


import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {

   List<SeatDto> seats;
   private ScreeningDto screening;
   private String username;
   private Integer price;
}
