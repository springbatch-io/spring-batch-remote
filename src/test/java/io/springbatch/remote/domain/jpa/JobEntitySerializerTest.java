package io.springbatch.remote.domain.jpa;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.springbatch.remote.domain.JobEntity;

import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

public class JobEntitySerializerTest {

	private JobEntitySerializer serializer;
	
	@Before
	public void before() {
		serializer = new JobEntitySerializer();
	}
	
	@Test
	public void testInvoke() throws Throwable {
		//mock
		MethodInvocation invocation = mock(MethodInvocation.class);
		//build a job entity
		JobEntity jobEntity = new JobEntity();
		jobEntity.setIncrementer(new RunIdIncrementer());
		Object[] arguments = new Object[]{jobEntity};
		//behavior
		when(invocation.getArguments()).thenReturn(arguments);
		when(invocation.proceed()).thenReturn(arguments[0]);
		//confirm that the string is set
		JobEntity result = (JobEntity) serializer.invoke(invocation);
		//check
		assertNotNull(result);
		assertTrue(StringUtils.isNotEmpty(result.getJobParametersIncrementer()));
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void testInvokeIterable() throws Throwable {
		//mock
		MethodInvocation invocation = mock(MethodInvocation.class);
		//build a job entity
		JobEntity jobEntity = new JobEntity();
		jobEntity.setIncrementer(new RunIdIncrementer());
		Object[] arguments = new Object[]{Arrays.asList(jobEntity)};
		//behavior
		when(invocation.getArguments()).thenReturn(arguments);
		when(invocation.proceed()).thenReturn(arguments[0]);
		//confirm that the string is set
		List<JobEntity> results = (List<JobEntity>) serializer.invoke(invocation);
		//check
		boolean evaluated = false;
		assertNotNull(results);
		for (JobEntity result : results) {
			assertTrue(StringUtils.isNotEmpty(result.getJobParametersIncrementer()));
			evaluated = true;
		}//end for
		assertTrue(evaluated);
		
	}	
}
