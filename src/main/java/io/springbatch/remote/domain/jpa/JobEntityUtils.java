package io.springbatch.remote.domain.jpa;

import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersValidator;

import com.thoughtworks.xstream.XStream;

public class JobEntityUtils {

	private XStream xstream = new XStream();
	
	public String serializeJobIncrementer(JobParametersIncrementer incrementer) {
		return xstream.toXML(incrementer);
	}
	
	public String serializeJobParametersValidator(JobParametersValidator validator) {
		return xstream.toXML(validator);
	}	

	public JobParametersIncrementer deserializeJobIncrement(String incrementer) {
		return (JobParametersIncrementer) xstream.fromXML(incrementer);
	}
	
	public JobParametersValidator deserializeJobParametersValidator(String validator) {
		return (JobParametersValidator) xstream.fromXML(validator);
	}	
}
