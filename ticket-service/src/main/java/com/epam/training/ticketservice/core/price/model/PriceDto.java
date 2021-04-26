package com.epam.training.ticketservice.core.price.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
