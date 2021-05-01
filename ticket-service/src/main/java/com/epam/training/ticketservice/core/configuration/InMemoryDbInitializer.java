package com.epam.training.ticketservice.core.configuration;

import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import com.epam.training.ticketservice.core.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Profile("! prod")
@RequiredArgsConstructor
public class InMemoryDbInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() throws Exception {
        userRepository.save(UserEntity.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .role(UserEntity.Role.ADMIN)
            .build());
    }

}
