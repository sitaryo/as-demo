package com.sendroids.usersyncclient;

import com.sendroids.usersynccore.entity.UserIdentity;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class UserBatchConfig<USER> {

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ItemReader<USER> reader;
    private final ItemWriter<UserIdentity> writer;
    private final SyncUserService<USER> syncUserService;

    @Value("${sync.user.job-name:sync-user-job}")
    private String jobName;

    @Value("${sync.user.step-name:sync-user-step}")
    private String stepName;

    @Value("${sync.user.chunk:100}")
    private int chunk;

    public UserBatchConfig(
            JobLauncher jobLauncher,
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            @NonNull ItemReader<USER> reader,
            @NonNull ItemWriter<UserIdentity> writer,
            SyncUserService<USER> syncUserService
    ) {
        this.jobLauncher = jobLauncher;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.reader = reader;
        this.writer = writer;
        this.syncUserService = syncUserService;
    }

    private Job syncUserJob() {
        var syncStep = stepBuilderFactory.get(stepName)
                .<USER, UserIdentity>chunk(chunk)
                .reader(reader)
                .processor((ItemProcessor<USER, UserIdentity>) syncUserService::createUser)
                .writer(writer)
                .build();
        return jobBuilderFactory.get(jobName)
                .incrementer(new RunIdIncrementer())
                .flow(syncStep)
                .end()
                .build();
    }

    @Scheduled(cron = "${sync.user.cron}")
    public void perform() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(syncUserJob(), params);
    }

}
