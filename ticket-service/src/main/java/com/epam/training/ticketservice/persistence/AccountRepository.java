package com.epam.training.ticketservice.persistence;

import com.epam.training.ticketservice.persistence.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountEntity,Integer> {
}
