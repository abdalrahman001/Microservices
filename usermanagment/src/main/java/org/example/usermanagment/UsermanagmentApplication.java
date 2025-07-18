package org.example.usermanagment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@SpringBootApplication
public class UsermanagmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsermanagmentApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @GetMapping("/")
    public String getMethodName() {
        return "Hello world" ;
    }
    
}
