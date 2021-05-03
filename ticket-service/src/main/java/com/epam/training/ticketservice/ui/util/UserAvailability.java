package com.epam.training.ticketservice.ui.util;

import org.springframework.shell.Availability;

public interface UserAvailability {

    Availability isUserAvailable();

    Availability isAdminAvailable();
}
