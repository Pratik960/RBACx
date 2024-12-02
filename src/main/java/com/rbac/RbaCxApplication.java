package com.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = "com.rbac")
public class RbaCxApplication {

	public static void main(String[] args) {
		SpringApplication.run(RbaCxApplication.class, args);
	}

}
