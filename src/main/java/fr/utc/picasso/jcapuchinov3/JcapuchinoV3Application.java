package fr.utc.picasso.jcapuchinov3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableScheduling
public class JcapuchinoV3Application {

    public static void main(String[] args) {
        SpringApplication.run(JcapuchinoV3Application.class, args);
    }

}
