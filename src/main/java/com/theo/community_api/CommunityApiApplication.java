package com.theo.community_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ConfigurationPropertiesScan
@EnableJpaAuditing
@SpringBootApplication
public class CommunityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApiApplication.class, args);
	}

}
