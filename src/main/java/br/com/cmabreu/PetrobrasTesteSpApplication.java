package br.com.cmabreu;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PetrobrasTesteSpApplication {
	
	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(PetrobrasTesteSpApplication.class);
		builder.headless(false);
		builder.run(args);
	}

}
