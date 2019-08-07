package com.polarising.projetoescola;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetoEscolaPolarising {

	public static StringNormalizer stringNormalizer = new StringNormalizer();

	public static void main(String[] args) {

		SpringApplication.run(ProjetoEscolaPolarising.class, args);

	}
}
