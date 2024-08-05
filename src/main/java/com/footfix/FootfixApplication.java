package com.footfix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // (exclude  = {DataSourceAutoConfiguration.class})
public class FootfixApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootfixApplication.class, args);
	}

}
