package ch.valtech.kubernetes.microservice.cluster.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"ch.valtech.kubernetes.microservice.cluster.persistence",
    "ch.valtech.kubernetes.microservice.cluster.security.config"})
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class PersistenceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PersistenceApplication.class, args);
  }

}
