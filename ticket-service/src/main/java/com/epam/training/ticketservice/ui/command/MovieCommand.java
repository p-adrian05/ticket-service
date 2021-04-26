package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.account.AccountService;
import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.movie.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class MovieCommand {

    private final MovieService movieService;
    private final AccountService accountService;

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
        if (accountService.getSignedInUser() == null) {
            return Availability.unavailable("Not logged in");
        } else if (accountService.getSignedInUser().isPrivileged()) {
            return Availability.available();
        }
        return Availability.unavailable("You are not an admin user");
    }

}
