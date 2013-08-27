package io.springbatch.remote.domain.jpa;

import org.springframework.batch.core.JobParametersIncrementer;

import com.thoughtworks.xstream.XStream;

public class JobEntityUtils {

	private XStream xstream = new XStream();
	
	public String serializeJobIncrementer(JobParametersIncrementer incrementer) {
		return xstream.toXML(incrementer);
	}

	public JobParametersIncrementer deserializeJobIncrement(String incrementer) {
		return (JobParametersIncrementer) xstream.fromXML(incrementer);
	}
}
