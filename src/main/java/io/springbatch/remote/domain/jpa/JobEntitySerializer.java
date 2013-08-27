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
				if (entity.getIncrementer() != null) {
					entity.setJobParametersIncrementer(jobEntityUtils.serializeJobIncrementer(entity.getIncrementer()));
				}//end for
			}//end for
		} else if (argument instanceof JobEntity) {
			if (((JobEntity)argument).getIncrementer() != null) {
				((JobEntity)argument).setJobParametersIncrementer(jobEntityUtils.serializeJobIncrementer(((JobEntity)argument).getIncrementer()));
			}//end for			
		}//end if
		return invocation.proceed();//invoke
	}

}
