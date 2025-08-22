package com.example.nesta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NestaApplication {
    public static void main(String[] args) {
        SpringApplication.run(NestaApplication.class, args);
    }

}
