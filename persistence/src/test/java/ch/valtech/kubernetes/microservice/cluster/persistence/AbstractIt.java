package ch.valtech.kubernetes.microservice.cluster.persistence;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
    "grpc.server.inProcessName=test",
    "grpc.server.port=-1",
    "grpc.client.inProcess.address=in-process:test"
})
@EmbeddedKafka(topics = "auditing", bootstrapServersProperty = "application.kafka.bootstrapAddress")
@DirtiesContext
public abstract class AbstractIt {

}
