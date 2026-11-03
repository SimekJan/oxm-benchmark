package cz.cuni.mff.simekjan.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = "cz.cuni.mff.simekjan.executor",
    excludeName = {
        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
        "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
    }
)
public class ExecutorApplication {

    static void main(String[] args) {
        SpringApplication.run(ExecutorApplication.class, args);
    }
}
