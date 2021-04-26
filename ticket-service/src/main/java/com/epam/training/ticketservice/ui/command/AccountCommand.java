package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.account.AccountService;
import com.epam.training.ticketservice.core.account.model.UserDto;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class AccountCommand {

    private final AccountService accountService;

    @ShellMethod(value = "Sign In Privileged", key = "sign in privileged")
    public String signInPrivileged(String username, String password) {
        try {
            accountService.signIn(username, password);
        } catch (BadCredentialsException e) {
            return "Login failed due to incorrect credentials";
        }
        return "Succesful sign in";
    }

    @ShellMethod(value = "Describe account", key = "describe account")
    public String describeAccount() {
        UserDto userDto = accountService.getSignedInUser();
        if (userDto == null) {
            return "You are not signed in";
        } else if (userDto.isPrivileged()) {
            return String.format("Signed in with privileged account %s", userDto.getUsername());
        }
        return String.format("You are signed in %s", userDto.getUsername());
    }

    @ShellMethod(value = "Account sign out", key = "sign out")
    public void signOut() {
        accountService.signOut();
    }
}
