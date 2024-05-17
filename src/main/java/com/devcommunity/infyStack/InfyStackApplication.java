package com.devcommunity.infyStack;

import com.devcommunity.infyStack.logging.InfyStackLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InfyStackApplication {

	public static void main(String[] args) {

		SpringApplication.run(InfyStackApplication.class, args);
		InfyStackLogger.info("InfyStack: App is running.");
	}

}
