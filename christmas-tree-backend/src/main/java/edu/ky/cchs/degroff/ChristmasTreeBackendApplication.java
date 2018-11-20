package edu.ky.cchs.degroff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class ChristmasTreeBackendApplication
    {

    public static void main( String[] args )
        {
        SpringApplication.run( ChristmasTreeBackendApplication.class, args );
        }
    }
