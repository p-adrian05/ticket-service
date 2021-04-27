package com.epam.training.ticketservice.core.user.model;

import lombok.Builder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class RegistrationDto {

    private final String username;

    private final String password;
}
