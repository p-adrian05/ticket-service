package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.exception.UserAlreadyExistsException;
import com.epam.training.ticketservice.core.user.model.RegistrationDto;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class UserCommand {

    private final UserService userService;
    private final LoginService loginService;

    @ShellMethod(value = "Sign In Privileged", key = "sign in privileged")
    public String signInPrivileged(String username, String password) {
        return handleError(() -> loginService.loginAsPrivileged(username, password),
            (userDto) -> String.format("Succesful sign in as privileged %s", userDto.getUsername()),
            "Login failed due to incorrect credentials");
    }

    @ShellMethod(value = "Sign In", key = "sign in")
    public String signIn(String username, String password) {
        return handleError(() -> loginService.login(username, password),
            (userDto) -> String.format("Succesful sign in %s", userDto.getUsername()),
            "Login failed due to incorrect credentials");
    }

    @ShellMethod(value = "Describe account", key = "describe account")
    public String describeAccount() {
        return handleError(loginService::getLoggedInUser, (userDto) -> userDto.getRole().equals(UserEntity.Role.ADMIN)
                ? String.format("Signed in with privileged account '%s'", userDto.getUsername()) :
                String.format("Signed in with account '%s'", userDto.getUsername()),
            "You are not signed in");
    }

    @ShellMethod(value = "Account sign up", key = "sign up")
    public String registerUser(String username, String password) {
        String message = "Registration was successful";
        try {
            userService.registerUser(new RegistrationDto(username, password));
        } catch (UserAlreadyExistsException e) {
            log.error("Username already exists during registration", e);
            message = "Registration failed, username already exists";
        } catch (Exception e) {
            log.error("Error during registration", e);
            message = "Registration failed";
        }
        return message;
    }

    @ShellMethod(value = "Account sign out", key = "sign out")
    public String signOut() {
        return handleError(loginService::logOut, (userDto) -> String.format("%s is logged out", userDto.getUsername()),
            "You need to sign in first");
    }

    private String handleError(Supplier<Optional<UserDto>> supplier,
                               Function<UserDto, String> successfulMessageProvider, String errorMessage) {
        Optional<UserDto> optionalUserDto = supplier.get();
        return optionalUserDto.isPresent() ? successfulMessageProvider.apply(optionalUserDto.get()) : errorMessage;
    }


}