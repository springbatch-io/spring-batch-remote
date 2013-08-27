package io.springbatch.remote.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.springbatch.remote.domain.JobEntity;
import io.springbatch.remote.domain.jpa.JpaJobEntityRepository;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/META-INF/spring/common/remote-registry-context.xml"})
@ActiveProfiles("junit")
public class RemoteJobRegistryIT {

	@Autowired
	@Qualifier("jobRegistry")
	private RemoteJobRegistry registry;
	
	@Autowired
	private JpaJobEntityRepository jobEntityRepository;
	
	@Autowired
	private DataSource dataSource;
	
	@After
	public void after() {
		jobEntityRepository.deleteAll();
		//'unregister'
		for (String name : registry.getJobNames()) {
			registry.unregister(name);
		}//end for
	}
	
	@Test
	public void testGetJobNames() {
		assertNotNull(registry.getJobNames());
	}

	@Transactional
	@Test
	public void testGetJob() throws Exception {
		//build a 'job entity record'
		JobEntity entity = new JobEntity("simpleJob");
		jobEntityRepository.save(entity);
		//get back a 'simpleJob'
		Job job = registry.getJob("simpleJob");
		assertNotNull(job);
		assertTrue(job instanceof SimpleJob);
	}

	@Transactional
	@Test
	public void testRegister() throws Exception {
		//register it in both the database AND the local
		Job job = new SimpleJob("simpleJob");
		JobFactory jobFactory = new ReferenceJobFactory(job);
		//register
		registry.register(jobFactory);
		//get it
		assertEquals(registry.getJob(job.getName()),job);
		//check in the database
		assertNotNull(jobEntityRepository.findByName(job.getName()));
	}

	
}
