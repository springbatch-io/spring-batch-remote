package io.springbatch.remote.domain.jpa;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.springbatch.remote.domain.JobEntity;

import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

public class JobEntityDeserializerTest {

	private JobEntityDeserializer deserializer;
	
	@Before
	public void before() {
		deserializer = new JobEntityDeserializer();
	}
	
	@Test
	public void testInvoke() throws Throwable{
		//mock
		MethodInvocation invocation = mock(MethodInvocation.class);
		JobEntity jobEntity = new JobEntity();
		//serialize the jobparameters
		String serialized = new JobEntityUtils().serializeJobIncrementer(new RunIdIncrementer());
		jobEntity.setJobParametersIncrementer(serialized);
		//behavior
		when(invocation.proceed()).thenReturn(jobEntity);
		//execute
		JobEntity result = (JobEntity) deserializer.invoke(invocation);
		//check
		assertNotNull(result.getIncrementer());
		assertNotNull(result.getJobParametersIncrementer());
		assertTrue(StringUtils.isNotEmpty(result.getJobParametersIncrementer()));
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInvokeIterable() throws Throwable{
		//mock
		MethodInvocation invocation = mock(MethodInvocation.class);
		JobEntity jobEntity = new JobEntity();
		Object results = Arrays.asList(jobEntity);
		//serialize the jobparameters
		String serialized = new JobEntityUtils().serializeJobIncrementer(new RunIdIncrementer());
		jobEntity.setJobParametersIncrementer(serialized);
		//behavior
		when(invocation.proceed()).thenReturn(results);
		//execute
		Iterable<JobEntity> result = (Iterable<JobEntity>) deserializer.invoke(invocation);
		//check
		assertTrue(result instanceof Iterable);
		assertNotNull(result);
		//init
		boolean evaluated = false;
		//loop
		for (JobEntity entity : result) {
			assertNotNull(entity.getIncrementer());
			assertNotNull(entity.getJobParametersIncrementer());
			assertTrue(StringUtils.isNotEmpty(entity.getJobParametersIncrementer()));
			evaluated = true;
		}//end for
		assertTrue(evaluated);
	}	

}
