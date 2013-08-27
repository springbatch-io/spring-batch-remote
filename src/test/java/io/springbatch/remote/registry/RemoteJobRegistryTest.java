package io.springbatch.remote.registry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.springbatch.remote.domain.JobEntity;
import io.springbatch.remote.domain.JobEntityRepository;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.launch.NoSuchJobException;

public class RemoteJobRegistryTest {

	private RemoteJobRegistry registry;
	
	private MapJobRegistry localJobRegistry;
	
	private JobEntityRepository jobEntityRepository;
	
	@Before
	public void before() {
		//mock
		localJobRegistry = mock(MapJobRegistry.class);
		jobEntityRepository = mock(JobEntityRepository.class);
		//set
		registry = new RemoteJobRegistry(localJobRegistry);
		registry.setJobEntityRepository(jobEntityRepository);
	}
	
	@Test
	public void testGetJobNames() {
		assertNotNull(registry.getJobNames());
		assertTrue(registry.getJobNames().isEmpty());
	}
	
	@Test
	public void testGetJobNamesFromRepository() {
		//behavior
		when(jobEntityRepository.findAll()).thenReturn(Arrays.asList(new JobEntity()));
		assertNotNull(registry.getJobNames());
		assertFalse(registry.getJobNames().isEmpty());
	}	

	@SuppressWarnings("unchecked")
	@Test(expected=NoSuchJobException.class)
	public void testGetJob() throws Exception {
		//behavior
		when(localJobRegistry.getJob(anyString())).thenThrow(NoSuchJobException.class);
		//execute
		registry.getJob("non-existent job");
	}

	@Test
	public void testRegister() throws Exception {
		//mock
		Job job = mock(Job.class);
		//execute
		registry.register(new ReferenceJobFactory(job));
	}
	
	@Test(expected=DuplicateJobException.class)
	public void testRegisterDuplicate() throws Exception {
		//mock
		Job job = mock(Job.class);
		//execute
		registry.register(new ReferenceJobFactory(job));
		//behavior
		doThrow(DuplicateJobException.class).when(localJobRegistry).register(any(JobFactory.class));
		//and again
		registry.register(new ReferenceJobFactory(job));
	}	

	@SuppressWarnings("unchecked")
	@Test
	public void testUnregister() throws Exception {
		//mock
		JobEntity entity = mock(JobEntity.class);
		Job job = mock(Job.class);
		//behavior
		when(entity.getJob()).thenReturn(job);
		when(localJobRegistry.getJob(anyString())).thenThrow(NoSuchJobException.class);
		when(jobEntityRepository.findByName(anyString())).thenReturn(entity);
		//check it's there
		assertTrue(registry.getJob("any name") instanceof Job);
		//now remove it
		registry.unregister("any name");
		//check 
		verify(localJobRegistry,atLeastOnce()).unregister(anyString());
		verify(jobEntityRepository,atLeastOnce()).delete(anyLong());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAfterPropertiesSet() throws Exception {
		//clear
		registry.setJobEntityRepository(null);
		//execute
		registry.afterPropertiesSet();
	}

}
