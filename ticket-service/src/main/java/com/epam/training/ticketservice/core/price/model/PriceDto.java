package com.epam.training.ticketservice.core.price.model;


import lombok.*;

import java.util.Currency;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class PriceDto {

   private final String name;

   private final Integer value;

   private final Currency currency;
}
