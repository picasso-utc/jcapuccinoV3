package fr.utc.picasso.jcapuchinov3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableScheduling
public class JcapuchinoV3Application {

    public static void main(String[] args) {
        SpringApplication.run(JcapuchinoV3Application.class, args);
    }

    /* allow cors for * */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}
