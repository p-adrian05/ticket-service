package com.epam.training.ticketservice.core.booking.model;


import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

   private Integer row;
   private Integer column;
   private ScreeningDto screening;
   private String username;
   private Integer price;
}
