package org.awiki.kamikaze.summit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class SummitApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummitApplication.class, args);
	}

}
