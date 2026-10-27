package cz.cuni.mff.simekjan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "cz.cuni.mff.simekjan.application")
public class Application {

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
