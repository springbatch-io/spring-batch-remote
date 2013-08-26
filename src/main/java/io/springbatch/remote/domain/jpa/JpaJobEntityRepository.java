package io.springbatch.remote.domain.jpa;

import io.springbatch.remote.domain.JobEntity;
import io.springbatch.remote.domain.JobEntityRepository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaJobEntityRepository extends JpaRepository<JobEntity, Long>, JobEntityRepository {

}