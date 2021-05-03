package com.epam.training.ticketservice.ui.util;

import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAvailability {

    private final LoginService loginService;

    public Availability isUserAvailable() {
        return isAvailable(UserEntity.Role.USER, "You are not a user");
    }

    public Availability isAdminAvailable() {
        return isAvailable(UserEntity.Role.ADMIN, "You are not an admin user");
    }

    private Availability isAvailable(UserEntity.Role role, String authorizationFailedMessage) {
        Optional<UserDto> loggedInUser = loginService.getLoggedInUser();
        if (loggedInUser.isEmpty()) {
            return Availability.unavailable("Not logged in");
        } else if (loggedInUser.get().getRole().equals(role)) {
            return Availability.available();
        }
        return Availability.unavailable(authorizationFailedMessage);
    }

}
