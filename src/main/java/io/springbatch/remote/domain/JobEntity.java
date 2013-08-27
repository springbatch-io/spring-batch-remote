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

import org.springframework.batch.core.JobParametersIncrementer;

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

	public JobEntity() { }
	
	public JobEntity(String name) {
		this();
		this.name = name;
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

	@Override
	public String toString() {
		return "JobEntity [id=" + id + ", version=" + version + ", name="
				+ name + "]";
	}
	
	
	
}
