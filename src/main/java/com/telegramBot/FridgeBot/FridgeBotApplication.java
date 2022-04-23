package com.telegramBot.FridgeBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FridgeBotApplication {
	private static final Logger log = LoggerFactory.getLogger(FridgeBotApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FridgeBotApplication.class, args);
	}
}
