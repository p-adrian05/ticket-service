package com.epam.training.ticketservice;

import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.screening.ScreeningService;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;


@SpringBootApplication
@RequiredArgsConstructor
public class Application   implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    private final MovieService movieService;
    private final ScreeningService screeningService;
    private final RoomService roomService;
    @Override
    public void run(String... args) throws Exception {
//        RoomDto roomDto = RoomDto.builder()
//            .name("TestRoom")
//            .columns(12)
//            .rows(23)
//            .build();
//        roomService.createRoom(roomDto);
//        roomService.readAllRooms().forEach(System.out::println);
//
//        MovieDto movieDto = MovieDto.builder()
//                .title("Test")
//                .duration(60)
//                .genre("Akcio")
//                .build();
//
//        movieService.createMovie(movieDto);
//
//
//
//        ScreeningDto screeningDto = ScreeningDto.builder()
//                .movieName("Test")
//                .roomName("TestRoom")
//                .time(LocalDateTime.of(2021,4,20,12,30))
//                .build();
//        screeningService.createScreening(screeningDto);
//        screeningDto.setTime(LocalDateTime.of(2021,4,20,13,41));
//        screeningService.createScreening(screeningDto);
//        movieService.deleteMovie("Test");



    }
}
