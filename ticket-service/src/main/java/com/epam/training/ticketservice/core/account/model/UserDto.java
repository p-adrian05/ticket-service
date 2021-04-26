package com.epam.training.ticketservice.core.account.model;


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
public class UserDto {

    private final String username;

    private final String password;

    private final boolean isPrivileged;

}
