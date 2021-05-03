package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.movie.exceptions.UnknownMovieException;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.ui.util.UserAvailability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class MovieCommand {

    private final MovieService movieService;
    private final UserAvailability userAvailability;

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
    public String createMovie(String title, String genre, int duration){
        MovieDto movieDto = MovieDto.builder()
            .genre(genre)
            .title(title)
            .duration(duration)
            .build();
        try{
            movieService.createMovie(movieDto);
            return movieDto.toString();
        }catch (MovieAlreadyExistsException e){
            log.error("Error during creating movie: "+e.getMessage());
            return e.getMessage();
        }

    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin delete Movie", key = "delete movieEntity")
    public String deleteMovie(String title){
        try{
            movieService.deleteMovie(title);
            return "Successful deletion";
        }catch (UnknownMovieException e){
            log.error("Error during deleting movie: "+e.getMessage());
            return e.getMessage();
        }
    }

    @ShellMethodAvailability("isAvailable")
    @ShellMethod(value = "Admin update Movie", key = "update movieEntity")
    public String updateMovie(String title, String genre, int duration){
        MovieDto movieDto = MovieDto.builder()
            .genre(genre)
            .title(title)
            .duration(duration)
            .build();
        try{
            movieService.updateMovie(movieDto);
            return movieDto.toString();
        }catch (UnknownMovieException e){
            log.error("Error during updating movie: "+e.getMessage());
            return e.getMessage();
        }
    }

    private Availability isAvailable() {
       return userAvailability.isAdminAvailable();
    }

}
