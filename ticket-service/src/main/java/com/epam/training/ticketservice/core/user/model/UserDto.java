package com.epam.training.ticketservice.core.user.model;


import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
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

    private final UserEntity.Role role;

}
