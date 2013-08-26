package io.springbatch.remote.domain;

import java.util.List;

/**
 * repository interface to interact with JobEntity
 * 
 *
 */
public interface JobEntityRepository {

	public JobEntity save(JobEntity jobEntity);
	
	public List<JobEntity> findAll();
	
	public JobEntity findByName(String name);
	
	public void delete(Long id);
	
}
