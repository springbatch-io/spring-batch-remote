package io.springbatch.remote.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.core.MessagingOperations;
import org.springframework.integration.core.PollableChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * message channel implementation of jobLauncher
 * @author 
 *
 */
public class MessageChannelJobLauncher implements JobLauncher, InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageChannelJobLauncher.class);

	public static String JOB_NAME_HEADER = "jobName";
	
	private MessagingOperations gateway;
	
	private PollableChannel replyChannel;
	
	private JobRepository jobRepository;
	
	private long timeout = 5 * 1000;//default timeout
	
	@Override
	public JobExecution run(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		//check if there's an instance
		JobExecution execution = jobRepository.getLastJobExecution(job.getName(), jobParameters);
		if (execution != null) {
			if (!job.isRestartable()) {
				throw new JobRestartException("JobInstance already exists and is not restartable");
			}//end if
		}//end if
		//validate the jobparameters locally
		if (job.getJobParametersValidator() != null) {
			job.getJobParametersValidator().validate(jobParameters);
		}//end if
		//execute
		//build
		Message<?> request = MessageBuilder.withPayload(jobParameters).setHeader(JOB_NAME_HEADER, job.getName()).build();
		//send
		logger.debug("sending start job for",job);
		gateway.send(request);
		//await response
		Message<?> reply = replyChannel.receive(timeout);
		logger.debug("after receiving timeout",reply);
		//fail
		if (reply == null) {
			throw new IllegalArgumentException("no response received");
		}//end if
		//convert
		if (reply.getPayload() instanceof JobExecution) {
			return (JobExecution) reply.getPayload();
		} else if (reply.getPayload() instanceof JobExecutionException) {
			//check, cast and throw
			if (reply.getPayload() instanceof JobExecutionAlreadyRunningException) {
				logger.error("exception thrown", reply.getPayload());
				throw (JobExecutionAlreadyRunningException) reply.getPayload();
			} else if (reply.getPayload() instanceof JobRestartException) {
				logger.error("exception thrown", reply.getPayload());
				throw (JobRestartException) reply.getPayload();
			} else if (reply.getPayload() instanceof JobInstanceAlreadyCompleteException) {
				logger.error("exception thrown", reply.getPayload());
				throw (JobInstanceAlreadyCompleteException) reply.getPayload();
			} else if (reply.getPayload() instanceof JobParametersInvalidException) {
				logger.error("exception thrown", reply.getPayload());
				throw (JobParametersInvalidException) reply.getPayload();
			} else {
				logger.error("exception thrown", reply.getPayload());
				throw new IllegalArgumentException((JobExecutionException) reply.getPayload());
			}//end if
		} else {
			logger.error("exception thrown", reply.getPayload());
			throw new IllegalArgumentException(reply.getPayload().toString());
		}//end if
	}

	public void setGateway(MessagingOperations gateway) {
		this.gateway = gateway;
	}

	public void setReplyChannel(PollableChannel replyChannel) {
		this.replyChannel = replyChannel;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(gateway,"A gateway must be set");
		Assert.notNull(replyChannel,"a reply channel must be set");
		Assert.notNull(jobRepository,"a jobRepository must be set");
	}

}
