package com.epam.training.ticketservice;

import com.epam.training.ticketservice.core.account.AccountService;
import com.epam.training.ticketservice.core.account.model.UserDto;
import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.booking.model.TicketDto;
import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.price.PriceService;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.screening.ScreeningService;
import com.epam.training.ticketservice.core.screening.model.ScreeningDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;


@SpringBootApplication
@RequiredArgsConstructor
public class Application   implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    private final MovieService movieService;
    private final ScreeningService screeningService;
    private final AccountService accountService;
    private final RoomService roomService;
    private final TicketService ticketService;
    private final PriceService priceService;
    @Override
    public void run(String... args) throws Exception {
        RoomDto roomDto = RoomDto.builder()
            .name("TestRoom")
            .columns(12)
            .rows(23)
            .build();
        roomService.createRoom(roomDto);
        PriceDto priceDto = PriceDto.builder().value(1500)
                .name("BasePrice")
                .currency(Currency.getInstance("HUF")).build();
        PriceDto priceDto2 = PriceDto.builder().value(500)
                .name("BasePrice2")
                .currency(Currency.getInstance("HUF")).build();
        priceService.createPrice(priceDto);
        priceService.createPrice(priceDto2);



        MovieDto movieDto = MovieDto.builder()
                .title("Test")
                .duration(60)
                .genre("Akcio")
                .build();

        movieService.createMovie(movieDto);

        UserDto user = UserDto.builder()
                .username("asd")
                .isPrivileged(true)
                .password("asd")
                .build();
        accountService.createAccount(user);


        ScreeningDto screeningDto = ScreeningDto.builder()
                .movieName("Test")
                .roomName("TestRoom")
                .time(LocalDateTime.of(2021,4,20,12,30))
                .build();
        screeningService.createScreening(screeningDto);
        priceService.attachMovie("Test","BasePrice");
        priceService.attachRoom("TestRoom","BasePrice2");
        priceService.attachScreening(screeningDto,"BasePrice2");
        TicketDto ticketDto = TicketDto.builder()
                .screening(screeningDto)
                .username("asd")
                .seats(new HashSet<>(List.of(SeatDto.of(1,2),new SeatDto(2,1),new SeatDto(3,3)))).build();
        System.out.println(ticketService.book(ticketDto));



//        PriceDto priceDto = PriceDto.builder()
//                .name("Name1")
//                .value(123).build();
//        priceService.createPrice(priceDto);
        //priceService.createPrice(priceDto);


    }
}
