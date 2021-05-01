package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.booking.TicketService;
import com.epam.training.ticketservice.core.booking.model.BookingDto;
import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.price.PriceService;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.model.RoomDto;
import com.epam.training.ticketservice.core.screening.ScreeningService;
import com.epam.training.ticketservice.core.screening.model.BasicScreeningDto;
import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.model.RegistrationDto;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

@Component
@Profile("! prod")
@RequiredArgsConstructor
public class InMemoryDbInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MovieService movieService;
    private final ScreeningService screeningService;
    private final RoomService roomService;
    private final TicketService ticketService;
    private final PriceService priceService;
    private final UserService accountService;
    @PostConstruct
    public void init() throws Exception{
        userRepository.save(UserEntity.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .role(UserEntity.Role.ADMIN)
            .build());
        RoomDto roomDto = RoomDto.builder()
            .name("TestRoom")
            .columns(12)
            .rows(23)
            .build();
        roomService.createRoom(roomDto);
        PriceDto priceDto = PriceDto.builder().value(1500)
            .name("Base")
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

        RegistrationDto user = RegistrationDto.builder()
            .username("asd")
            .password("asd")
            .build();
        accountService.registerUser(user);

        BasicScreeningDto screeningDto = BasicScreeningDto.builder()
            .movieName("Test")
            .roomName("TestRoom")
            .time(LocalDateTime.of(2021,4,20,12,30))
            .build();
        BasicScreeningDto screeningDto2 = BasicScreeningDto.builder()
            .movieName("Test")
            .roomName("TestRoom")
            .time(LocalDateTime.of(2021,4,20,16,30))
            .build();
        screeningService.createScreening(screeningDto);
        screeningService.createScreening(screeningDto2);
        priceService.attachMovie("Test","Base");
        priceService.attachRoom("TestRoom","BasePrice2");
        priceService.attachScreening(screeningDto,"BasePrice2");
        BookingDto bookingDto = BookingDto.builder()
            .screening(screeningDto)
            .seats(new HashSet<>(List.of(SeatDto.of(1,2),new SeatDto(1,1),new SeatDto(3,3)))).build();
        BookingDto bookingDto2 = BookingDto.builder()
            .screening(screeningDto2)
            .seats(new HashSet<>(List.of(SeatDto.of(1,2),new SeatDto(1,1),new SeatDto(3,3)))).build();
        System.out.println(ticketService.showPrice(bookingDto2,"EUR"));
        System.out.println(ticketService.showPrice(bookingDto2,"HUF"));
        System.out.println(ticketService.book(bookingDto,"asd","HUF"));
        System.out.println(ticketService.book(bookingDto2,"asd","EUR"));
       ticketService.getTicketsByUsername("asd").forEach(ticketDto -> System.out.println(ticketDto));

    }

}
