package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.user.LoginService;
import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.movie.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.user.model.UserDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class MovieCommand {

    private final MovieService movieService;
    private final LoginService loginService;

    @ShellMethod(value = "Movie List", key = "list movies")
    public List<String> listMovies() {
        List<MovieDto> movies = movieService.getMovies();
        if (movies.size() == 0) {
            return List.of("There are no movies at the moment");
        }
        return movies.stream().map(MovieDto::toString).collect(Collectors.toList());
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin create Movie", key = "create movieEntity")
    public MovieDto createMovie(String title, String genre, int duration) throws MovieAlreadyExistsException {
        MovieDto movieDto = MovieDto.builder()
            .genre(genre)
            .title(title)
            .duration(duration)
            .build();
        movieService.createMovie(movieDto);
        return movieDto;
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin delete Movie", key = "delete movieEntity")
    public void deleteMovie(String title) throws UnknownMovieException {
        movieService.deleteMovie(title);
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin update Movie", key = "update movieEntity")
    public MovieDto updateMovie(String title, String genre, int duration) throws UnknownMovieException {
        MovieDto movieDto = MovieDto.builder()
            .genre(genre)
            .title(title)
            .duration(duration)
            .build();
        movieService.updateMovie(movieDto);
        return movieDto;
    }

    private Availability isAvailable() {
        Optional<UserDto> loggedInUser = loginService.getLoggedInUser();
        if (loggedInUser.isEmpty()) {
            return Availability.unavailable("Not logged in");
        } else if (loggedInUser.get().getRole().equals(UserEntity.Role.ADMIN)) {
            return Availability.available();
        }
        return Availability.unavailable("You are not an admin user");
    }

}
