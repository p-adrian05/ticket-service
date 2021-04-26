package com.epam.training.ticketservice.core.account.persistence.repository;

import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<AccountEntity,Integer> {

    Optional<AccountEntity> findByUsername(String username);

    boolean existsByUsername(String username);

}
