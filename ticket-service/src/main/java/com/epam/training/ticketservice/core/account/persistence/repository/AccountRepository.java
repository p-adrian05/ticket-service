package com.epam.training.ticketservice.core.account.persistence.repository;

import com.epam.training.ticketservice.core.account.persistence.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountEntity,Integer> {
}
