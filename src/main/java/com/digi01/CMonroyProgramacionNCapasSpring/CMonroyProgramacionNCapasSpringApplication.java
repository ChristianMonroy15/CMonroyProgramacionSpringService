package com.digi01.CMonroyProgramacionNCapasSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class CMonroyProgramacionNCapasSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CMonroyProgramacionNCapasSpringApplication.class, args);
	}
}

