package io.springbatch.remote.domain.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.springbatch.remote.domain.JobEntity;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/META-INF/spring/common/remote-registry-context.xml"})
@ActiveProfiles("junit")
public class JpaJobEntityRepositoryIT {

	@Autowired
	private JpaJobEntityRepository jobEntityRepository;
	
	@After
	public void after() {
		jobEntityRepository.deleteAll();
	}
	
	@Transactional
	@Test
	public void testSave() {
		//build an entity
		JobEntity entity = new JobEntity("test_job");
		entity.setIncrementer(new RunIdIncrementer());
		//save
		jobEntityRepository.save(entity);
		//check its in the string
		assertNotNull(entity.getJobParametersIncrementer());
		assertTrue(entity.getJobParametersIncrementer().length() > 0);
	}

	@Transactional
	@Test
	public void testSaveIterable() {
		//build an entity
		JobEntity entity = new JobEntity("test_job");
		entity.setIncrementer(new RunIdIncrementer());		
		//entities
		List<JobEntity> entities = Arrays.asList(entity);
		//execute
		jobEntityRepository.save(entities);
		//now check
		boolean evaluated = false;
		for (JobEntity jobEntity : entities) {
			assertTrue(jobEntity.getJobParametersIncrementer().length() > 0);
			evaluated = true;
		}//end for
		//check
		assertTrue(evaluated);
	}

	@Transactional
	@Test
	public void testFindAll() {
		//store
		testSave();
		//now retrieve
		List<JobEntity> entities = jobEntityRepository.findAll();
		assertNotNull(entities);
		assertFalse(entities.isEmpty());
		boolean evaluated = false;
		//loop
		for (JobEntity entity : entities) {
			assertNotNull(entity.getIncrementer());
			evaluated = true;
		}//end for
		assertTrue(evaluated);
	}
}
