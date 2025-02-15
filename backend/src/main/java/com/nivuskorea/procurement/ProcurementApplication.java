package com.nivuskorea.procurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProcurementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcurementApplication.class, args);
	}

}
