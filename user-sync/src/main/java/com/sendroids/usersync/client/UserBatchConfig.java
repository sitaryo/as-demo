package com.sendroids.usersync.client;

import com.sendroids.usersync.core.entity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class UserBatchConfig<USER> {

    @Autowired private JobLauncher jobLauncher;
    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private ItemReader<USER> reader;
    @Autowired private ItemProcessor<USER, UserIdentity> processor;
    @Autowired private ItemWriter<UserIdentity> writer;

    @Value("${sync.user.job-name:sync-user-job}")
    private String jobName;

    @Value("${sync.user.step-name:sync-user-step}")
    private String stepName;

    @Value("${sync.user.chunk:100}")
    private int chunk;

    private Job syncUserJob() {
        var syncStep = stepBuilderFactory.get(stepName)
                .<USER, UserIdentity>chunk(chunk)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
        return jobBuilderFactory.get(jobName)
                .incrementer(new RunIdIncrementer())
                .flow(syncStep)
                .end()
                .build();
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void perform() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(syncUserJob(), params);
    }

}
