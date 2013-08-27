package io.springbatch.remote.launcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.support.PropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)//after each method to clear spring int context
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/META-INF/spring/server/remote-job-launcher-context.xml"})
@ActiveProfiles("junit")
public class MessageChannelJobLauncherIT {

	private static final Logger logger = LoggerFactory.getLogger(MessageChannelJobLauncherIT.class);
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private JobOperator jobOperator;
	
	@Autowired
	@Qualifier("job.start.request.channel")
	private AbstractSubscribableChannel outboundChannel;
	
	@Autowired
	@Qualifier("job.start.reply.channel")
	private AbstractSubscribableChannel inboundChannel;
	
	private Deserializer<?> deserializer = new DefaultDeserializer();
	
	@SuppressWarnings("rawtypes")
	private Serializer serializer = new DefaultSerializer();
	
	@Autowired
	private JobRegistry jobRegistry;
	
	@Test
	public void testRun() throws Exception {
		//subscribe to the outbound
		SimpleHandler handler = new SimpleHandler();
		outboundChannel.subscribe(handler);
		final SimpleJob job = new SimpleJob("test job");
		final JobParameters jobParameters = new JobParametersBuilder().addLong("runtime",System.currentTimeMillis()).toJobParameters();
		//invoke the job launcher 
		LauncherWrapper wrapper = new LauncherWrapper(job,jobParameters);
		new Thread(wrapper).start();
		//listen to outbound
		while (!handler.received) {
			Thread.sleep(100);
		}//end while
		//get the message
		assertNotNull(handler.message);
		assertTrue(handler.message.getHeaders().containsKey(MessageChannelJobLauncher.JOB_NAME_HEADER));
		byte[] serializedPayload = (byte[]) handler.message.getPayload();
		//convert
		JobParameters payload = (JobParameters) deserializer.deserialize(new ByteArrayInputStream(serializedPayload));
		assertEquals(payload,jobParameters);
		//TODO send a 'reply' on the inbound
		logger.info("end of test");
	}
	
	@Test
	public void testRunOperator() throws Exception {
		//subscribe to the outbound
		SimpleHandler handler = new SimpleHandler();
		outboundChannel.subscribe(handler);
		final SimpleJob job = new SimpleJob("test job");
		final JobParameters jobParameters = new JobParametersBuilder().addLong("runtime",System.currentTimeMillis()).toJobParameters();
		//register
		jobRegistry.register(new ReferenceJobFactory(job));
		//invoke the job launcher 
		OperatorWrapper wrapper = new OperatorWrapper(job,jobParameters);
		new Thread(wrapper).start();
		//listen to outbound
		while (!handler.received) {
			Thread.sleep(100);
		}//end while
		//get the message
		assertNotNull(handler.message);
		assertTrue(handler.message.getHeaders().containsKey(MessageChannelJobLauncher.JOB_NAME_HEADER));
		byte[] serializedPayload = (byte[]) handler.message.getPayload();
		//convert
		JobParameters payload = (JobParameters) deserializer.deserialize(new ByteArrayInputStream(serializedPayload));
		assertEquals(payload,jobParameters);
		//TODO send a 'reply' on the inbound
		logger.info("end of test");
	}	

	@SuppressWarnings("unchecked")
	@Test
	public void testRunFailure() throws Exception {
		//subscribe to the outbound
		SimpleHandler handler = new SimpleHandler();
		outboundChannel.subscribe(handler);
		final SimpleJob job = new SimpleJob("test job");
		final JobParameters jobParameters = new JobParametersBuilder().addLong("runtime",System.currentTimeMillis()).toJobParameters();
		//invoke the job launcher 
		LauncherWrapper wrapper = new LauncherWrapper(job,jobParameters);
		new Thread(wrapper).start();
		//listen to outbound
		while (!handler.received) {
			Thread.sleep(100);
		}//end while
		//get the message
		assertNotNull(handler.message);
		assertTrue(handler.message.getHeaders().containsKey(MessageChannelJobLauncher.JOB_NAME_HEADER));
		byte[] serializedPayload = (byte[]) handler.message.getPayload();
		//convert
		JobParameters payload = (JobParameters) deserializer.deserialize(new ByteArrayInputStream(serializedPayload));
		assertEquals(payload,jobParameters);
		//send a 'reply' on the inbound
		JobExecutionAlreadyRunningException e = new JobExecutionAlreadyRunningException("test failure");
		//serialize
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		serializer.serialize(e, bos);
		//create a message
		Message<?> message = MessageBuilder.withPayload(bos.toByteArray()).build();
		//send
		inboundChannel.send(message);
		//lets see what happens
		while (!wrapper.thrown) {
			Thread.sleep(100);
		}//end while
		//get
		assertNotNull(wrapper.exception);
		assertTrue(wrapper.exception instanceof JobExecutionAlreadyRunningException);
	}	
	
	
	class LauncherWrapper implements Runnable {
		
		Exception exception;
		
		boolean thrown = false;

		final Job job;
		
		final JobParameters jobParameters;
		
		LauncherWrapper(Job job,JobParameters jobParameters) {
			this.job = job;
			this.jobParameters = jobParameters;
		}
		
		@Override
		public void run() {
			try {
				jobLauncher.run(job, jobParameters);
			}
			catch (Exception e) {
				thrown = true;
				exception = e;
			}
		}
		
	}
	
	class OperatorWrapper implements Runnable {
		
		Exception exception;
		
		boolean thrown = false;

		final Job job;
		
		final JobParameters jobParameters;
		
		OperatorWrapper(Job job,JobParameters jobParameters) {
			this.job = job;
			this.jobParameters = jobParameters;
		}
		
		@Override
		public void run() {
			try {
				String properties = PropertiesConverter.propertiesToString(new DefaultJobParametersConverter().getProperties(jobParameters));
				System.out.println(properties);
				//execute
				jobOperator.start(job.getName(), properties);
			}
			catch (Exception e) {
				thrown = true;
				exception = e;
			}
		}
		
	}	
	
	
	class SimpleHandler implements MessageHandler {
		
		boolean received = false;
		
		Message<?> message = null;

		@Override
		public void handleMessage(Message<?> message) throws MessagingException {
			received = true;
			this.message = message;
		}
	}
	
}
