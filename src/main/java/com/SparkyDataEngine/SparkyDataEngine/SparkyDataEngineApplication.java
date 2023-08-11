package com.SparkyDataEngine.SparkyDataEngine;

import com.SparkyDataEngine.SparkyDataEngine.Controller.EngineController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

@SpringBootApplication
public class SparkyDataEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SparkyDataEngineApplication.class, args);
	}

}
