package com.epam.training.ticketservice.ui.configuration;

import com.epam.training.ticketservice.core.account.AccountService;
import com.epam.training.ticketservice.core.account.persistence.repository.AccountRepository;
import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.exceptions.MovieAlreadyExistsException;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.entity.GenreEntity;
import com.epam.training.ticketservice.core.movie.persistence.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("! prod")
@RequiredArgsConstructor
public class InMemoryDbInitializer {

    private final MovieService movieService;
    private final AccountService accountService;


    @PostConstruct
    public void init() throws Exception {
        MovieDto movieDto = MovieDto.builder()
            .title("Test")
            .duration(60)
            .genre("Akcio")
            .build();
        MovieDto movieDto2 = MovieDto.builder()
            .title("Test2")
            .duration(70)
            .genre("Drama")
            .build();
        movieService.createMovie(movieDto);
        movieService.createMovie(movieDto2);
    }

}
