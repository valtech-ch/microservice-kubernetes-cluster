package ch.valtech.kubernetes.microservice.cluster.filestorage;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.config.ReleaseToggleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(ReleaseToggleConfiguration.class)
@ComponentScan(basePackages = {"ch.valtech.kubernetes.microservice.cluster.filestorage",
    "ch.valtech.kubernetes.microservice.cluster.security.config"})
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class FileStorageApplication {

  public static void main(String[] args) {
    SpringApplication.run(FileStorageApplication.class, args);
  }

}
