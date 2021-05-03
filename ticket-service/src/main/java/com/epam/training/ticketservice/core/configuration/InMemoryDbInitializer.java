package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.booking.model.SeatDto;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Generated
@Profile("! prod")
@RequiredArgsConstructor
public class InMemoryDbInitializer {

    private final UserRepository userRepository;
    private final PriceRepository priceRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        userRepository.save(UserEntity.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .role(UserEntity.Role.ADMIN)
            .build());
        priceRepository.save(PriceEntity
            .builder()
            .currency("HUF")
            .name("Base")
            .value(1500)
            .build());
    }

}
