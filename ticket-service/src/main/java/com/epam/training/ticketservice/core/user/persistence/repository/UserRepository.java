package com.epam.training.ticketservice.core.user.persistence.repository;

import com.epam.training.ticketservice.core.user.persistence.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity,Integer> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUsernameAndRole(String username,UserEntity.Role role);

    boolean existsByUsername(String username);

}
