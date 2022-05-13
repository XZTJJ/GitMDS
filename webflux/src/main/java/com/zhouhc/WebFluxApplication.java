package com.zhouhc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WebFluxApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(WebFluxApplication.class)
                .properties("spring.config.location=classpath:/webflux.yml").run(args);
    }
}
