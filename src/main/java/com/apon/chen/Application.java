package com.apon.chen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * @author chen
 */

@SpringBootApplication

public class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
    @Override
    public SpringApplicationBuilder createSpringApplicationBuilder() {
        return new SpringApplicationBuilder(Application.class);
    }



}
