package io.springbatch.remote.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.job.SimpleJob;

/**
 * simple object to record a job name with a version
 * 
 * 
 *
 */
@Entity
@Table(name="batch_job_entity")
public class JobEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="job_entity_sequence")
	@SequenceGenerator(name="job_entity_sequence",sequenceName="batch_job_entity_seq")
	private Long id;
	
	@Column(name="job_entity_version")
	private Integer version = 0;
	
	@Column(name="job_name")
	private String name;
	
	@Transient
	private JobParametersIncrementer incrementer;
	
	@Column(name="job_parameters_incrementer")
	private String jobParametersIncrementer;
	
	@Transient
	private JobParametersValidator validator;
	
	@Column(name="job_parameters_validator")
	private String jobParametersValidator;

	@Column(name="restartable")
	private boolean restartable;
	
	public JobEntity() { }
	
	public JobEntity(String name) {
		this();
		this.name = name;
	}
	
	public JobEntity(Job job) {
		this();
		this.name = job.getName();
		this.incrementer = job.getJobParametersIncrementer();
		this.validator = job.getJobParametersValidator();
		this.restartable = job.isRestartable();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public JobParametersIncrementer getIncrementer() {
		return incrementer;
	}

	public void setIncrementer(JobParametersIncrementer incrementer) {
		this.incrementer = incrementer;
	}

	public String getJobParametersIncrementer() {
		return jobParametersIncrementer;
	}

	public void setJobParametersIncrementer(String jobParametersIncrementer) {
		this.jobParametersIncrementer = jobParametersIncrementer;
	}

	public JobParametersValidator getValidator() {
		return validator;
	}

	public void setValidator(JobParametersValidator validator) {
		this.validator = validator;
	}

	public String getJobParametersValidator() {
		return jobParametersValidator;
	}

	public void setJobParametersValidator(String jobParametersValidator) {
		this.jobParametersValidator = jobParametersValidator;
	}

	public boolean isRestartable() {
		return restartable;
	}

	public void setRestartable(boolean restartable) {
		this.restartable = restartable;
	}

	public Job getJob() {
		//create a simpleJob
		SimpleJob job = new SimpleJob(this.getName());
		job.setJobParametersIncrementer(this.getIncrementer());
		job.setJobParametersValidator(this.getValidator());
		job.setRestartable(this.isRestartable());
		//return
		return job;
	}
	
	@Override
	public String toString() {
		return "JobEntity [id=" + id + ", version=" + version + ", name="
				+ name + "]";
	}
	
}
