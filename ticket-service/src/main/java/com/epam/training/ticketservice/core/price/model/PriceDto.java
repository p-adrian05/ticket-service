package com.epam.training.ticketservice.core.price.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceDto {

   private String name;

   private Integer value;

   private Currency currency;
}
