package io.springbatch.remote.launcher;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.integration.Message;
import org.springframework.integration.core.MessagingOperations;
import org.springframework.integration.core.PollableChannel;

public class MessageChannelJobLauncherTest {

	private MessageChannelJobLauncher jobLauncher;
	
	private MessagingOperations gateway;

	private PollableChannel replyChannel;
	
	@Before
	public void before() throws Exception {
		gateway = mock(MessagingOperations.class);
		replyChannel = mock(PollableChannel.class);
		jobLauncher = new MessageChannelJobLauncher();
		jobLauncher.setGateway(gateway);
		jobLauncher.setReplyChannel(replyChannel);
		//check
		jobLauncher.afterPropertiesSet();
	}
	
	@Test
	public void testRun() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		Message message = mock(Message.class);
		JobExecution execution = mock(JobExecution.class);
		//behavior
		when(replyChannel.receive(anyLong())).thenReturn(message);
		when(message.getPayload()).thenReturn(execution);
		//execute
		jobLauncher.run(job,jobParameters);
	}
	
	@Test(expected=JobExecutionAlreadyRunningException.class)
	public void testRunFailure() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		Message message = mock(Message.class);
		JobExecutionAlreadyRunningException exception = mock(JobExecutionAlreadyRunningException.class);
		//behavior
		when(replyChannel.receive(anyLong())).thenReturn(message);
		when(message.getPayload()).thenReturn(exception);
		//execute
		jobLauncher.run(job,jobParameters);
	}
	
	@Test(expected=JobRestartException.class)
	public void testRunFailureJobRestartException() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		Message message = mock(Message.class);
		JobRestartException exception = mock(JobRestartException.class);
		//behavior
		when(replyChannel.receive(anyLong())).thenReturn(message);
		when(message.getPayload()).thenReturn(exception);
		//execute
		jobLauncher.run(job,jobParameters);
	}	
	
	@Test(expected=JobInstanceAlreadyCompleteException.class)
	public void testRunFailureJobInstanceAlreadyCompleteException() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		Message message = mock(Message.class);
		JobInstanceAlreadyCompleteException exception = mock(JobInstanceAlreadyCompleteException.class);
		//behavior
		when(replyChannel.receive(anyLong())).thenReturn(message);
		when(message.getPayload()).thenReturn(exception);
		//execute
		jobLauncher.run(job,jobParameters);
	}		
	
	@Test(expected=JobParametersInvalidException.class)
	public void testRunFailureJobParametersInvalidException() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		Message message = mock(Message.class);
		JobParametersInvalidException exception = mock(JobParametersInvalidException.class);
		//behavior
		when(replyChannel.receive(anyLong())).thenReturn(message);
		when(message.getPayload()).thenReturn(exception);
		//execute
		jobLauncher.run(job,jobParameters);
	}
	
	@Test(expected=Exception.class)
	public void testRunFailureException() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		Message message = mock(Message.class);
		Exception exception = mock(Exception.class);
		//behavior
		when(replyChannel.receive(anyLong())).thenReturn(message);
		when(message.getPayload()).thenReturn(exception);
		//execute
		jobLauncher.run(job,jobParameters);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testRunFailureJobExecutionException() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		Message message = mock(Message.class);
		JobExecutionException exception = mock(JobExecutionException.class);
		//behavior
		when(replyChannel.receive(anyLong())).thenReturn(message);
		when(message.getPayload()).thenReturn(exception);
		//execute
		jobLauncher.run(job,jobParameters);
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void testRunNull() throws Exception {
		//mock
		Job job = mock(Job.class);
		JobParameters jobParameters = mock(JobParameters.class);
		//execute
		jobLauncher.run(job,jobParameters);
	}	

}
