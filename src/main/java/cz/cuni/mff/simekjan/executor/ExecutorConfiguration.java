package cz.cuni.mff.simekjan.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfiguration {

    @Bean("datasetBuildExecutor")
    public Executor datasetBuildExecutor(
        @Value("${executor.build-concurrency:2}") int buildConcurrency
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(buildConcurrency);
        executor.setMaxPoolSize(buildConcurrency);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("dataset-build-");
        executor.initialize();
        return executor;
    }
}
