package ch.valtech.kubernetes.microservice.cluster.persistence;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MariaDBContainer;

@SpringBootTest(properties = {
    "grpc.server.inProcessName=test",
    "grpc.server.port=-1",
    "grpc.client.inProcess.address=in-process:test"
})
@EmbeddedKafka(topics = "auditing", bootstrapServersProperty = "application.kafka.bootstrapAddress")
@DirtiesContext
public abstract class AbstractIt {

  static final MariaDBContainer MARIADB_CONTAINER;

  static {
    MARIADB_CONTAINER = new MariaDBContainer("mariadb:10.6")
        .withDatabaseName("messaging")
        .withUsername("sa")
        .withPassword("password");
    MARIADB_CONTAINER.start();
    System.setProperty("DB_URL", MARIADB_CONTAINER.getJdbcUrl());
    System.setProperty("DB_USERNAME", MARIADB_CONTAINER.getUsername());
    System.setProperty("DB_PASSWORD", MARIADB_CONTAINER.getPassword());
    System.setProperty("R2DBC_URL", MARIADB_CONTAINER.getJdbcUrl()
        .replace("jdbc", "r2dbc"));
  }

}
