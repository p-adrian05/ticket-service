package com.epam.training.ticketservice.model;


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
   private Screening screening;
   private String username;
   private Integer price;
}
