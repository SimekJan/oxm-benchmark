package cz.cuni.mff.simekjan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = "cz.cuni.mff.simekjan",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "cz\\.cuni\\.mff\\.simekjan\\.executor\\..*"
    )
)
@EntityScan(basePackages = "cz.cuni.mff.simekjan.application")
public class Application {

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
