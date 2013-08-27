package io.springbatch.remote.domain.jpa;

import io.springbatch.remote.domain.JobEntity;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

public class JobEntityDeserializer implements MethodInterceptor {

	private JobEntityUtils jobEntityUtils = new JobEntityUtils();
	
	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//invoke the object
		Object result = invocation.proceed();
		//deserialize
		if (result instanceof JobEntity) {
			if (StringUtils.isNotEmpty(((JobEntity)result).getJobParametersIncrementer())) {
				((JobEntity)result).setIncrementer(jobEntityUtils.deserializeJobIncrement(((JobEntity)result).getJobParametersIncrementer()));
			}//end if
		} else if (result instanceof Iterable) {
			for (JobEntity entity : (Iterable<JobEntity>) result) {
				if (StringUtils.isNotEmpty(entity.getJobParametersIncrementer())) {
					entity.setIncrementer(jobEntityUtils.deserializeJobIncrement(entity.getJobParametersIncrementer()));
				}//end if				
			}//end for
		}//end if
		//return
		return result;
	}

}
