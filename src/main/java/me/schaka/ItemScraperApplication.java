package me.schaka;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.schaka.util.FileCopyUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.file.Paths;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class ItemScraperApplication {

	@Bean
	public Module javaTimeModule() {
		return new JavaTimeModule();
	}

	@Bean
	public TaskExecutor taskExecutor(){
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setQueueCapacity(10);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	public static void main(String[] args) throws Exception {

		FileCopyUtils.copyFolderContentFromClasspath("config/analysis", Paths.get(System.getProperty("user.dir") + "/config/analysis"));
		SpringApplication.run(ItemScraperApplication.class, args);
	}
}
