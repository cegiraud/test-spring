package com.soprasteria.initiatives.repository;

import com.soprasteria.initiatives.domain.IdentifiableEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Generic repository
 *
 * @author jntakpe
 */
public interface GenericRepository<T extends IdentifiableEntity> extends ReactiveMongoRepository<T, String> {

}
