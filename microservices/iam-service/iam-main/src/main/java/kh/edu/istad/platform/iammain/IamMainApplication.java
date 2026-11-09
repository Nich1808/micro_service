package kh.edu.istad.platform.iammain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "kh.edu.istad.platform")
@EntityScan(basePackages = "kh.edu.istad.platform.iampersistence.entity")
@EnableJpaRepositories(basePackages = "kh.edu.istad.platform.iampersistence.repository")
public class IamMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamMainApplication.class, args);
    }
}
