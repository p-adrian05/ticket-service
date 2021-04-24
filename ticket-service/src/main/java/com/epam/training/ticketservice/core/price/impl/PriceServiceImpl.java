package com.epam.training.ticketservice.core.price.impl;

import com.epam.training.ticketservice.core.price.PriceService;
import com.epam.training.ticketservice.core.price.exceptions.PriceAlreadyExistsException;
import com.epam.training.ticketservice.core.price.exceptions.UnknownPriceException;
import com.epam.training.ticketservice.core.price.model.PriceDto;
import com.epam.training.ticketservice.core.price.persistence.entity.PriceEntity;
import com.epam.training.ticketservice.core.price.persistence.repository.PriceRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    public void createPrice(PriceDto priceDto) throws PriceAlreadyExistsException {
        Objects.requireNonNull(priceDto, "Price cannot be null");
        Objects.requireNonNull(priceDto.getName(), "Price name cannot be null");
        Objects.requireNonNull(priceDto.getValue(), "Price value cannot be null");
        Objects.requireNonNull(priceDto.getCurrency(), "Currency value cannot be null");
        if(priceRepository.existsByName(priceDto.getName())){
            throw new PriceAlreadyExistsException(String.format("Price already exists by name: %s",priceDto.getName()));
        }
        log.debug("Creating new Price : {}",priceDto);
        PriceEntity priceEntity = PriceEntity.builder()
                .currency(priceDto.getCurrency().getDisplayName())
                .name(priceDto.getName())
                .value(priceDto.getValue())
                .build();
        PriceEntity createdPrice = priceRepository.save(priceEntity);
        log.debug("Created Price is : {}",createdPrice);
    }

    @Override
    public void updatePrice(PriceDto priceDto) throws UnknownPriceException {
        Objects.requireNonNull(priceDto, "Price cannot be null");
        Objects.requireNonNull(priceDto.getName(), "Price name cannot be null");
        Objects.requireNonNull(priceDto.getValue(), "Price value cannot be null");
        Optional<PriceEntity> priceEntity = priceRepository.findByName(priceDto.getName());
        if(priceEntity.isEmpty()){
            throw new UnknownPriceException(String.format("Price not found by name: %s",priceDto.getName()));
        }
        log.debug("Price before update: {}",priceEntity.get());
        priceEntity.get().setValue(priceDto.getValue());
        priceRepository.save(priceEntity.get());
        log.debug("Updated Price is : {}",priceEntity.get());
    }
}
