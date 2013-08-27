package io.springbatch.remote.domain.jpa;

import io.springbatch.remote.domain.JobEntity;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class JobEntitySerializer implements MethodInterceptor {

	private JobEntityUtils jobEntityUtils = new JobEntityUtils();
	
	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//serialize the jobParameters
		Object argument = invocation.getArguments()[0];//basic invocation
		//check the type
		if (argument instanceof Iterable) {
			//go through and serialize
			for (JobEntity entity : (Iterable<JobEntity>) argument) {
				serialize(entity);
			}//end for
		} else if (argument instanceof JobEntity) {
			serialize((JobEntity)argument);
		}//end if
		return invocation.proceed();//invoke
	}

	
	protected void serialize(JobEntity entity) {
		if (entity.getIncrementer() != null) {
			entity.setJobParametersIncrementer(jobEntityUtils.serializeJobIncrementer(entity.getIncrementer()));
		}//end for
		if (entity.getValidator() != null) {
			entity.setJobParametersValidator(jobEntityUtils.serializeJobParametersValidator(entity.getValidator()));
		}//end for		
	}
}
