package io.springbatch.remote.domain.jpa;

import io.springbatch.remote.domain.JobEntity;
import io.springbatch.remote.domain.JobEntityRepository;

import org.springframework.data.repository.CrudRepository;

public interface JpaJobEntityRepository extends CrudRepository<JobEntity, Long>, JobEntityRepository {

}