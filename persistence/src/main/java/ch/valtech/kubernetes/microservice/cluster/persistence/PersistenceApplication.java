package ch.valtech.kubernetes.microservice.cluster.persistence;

import io.grpc.netty.shaded.io.netty.buffer.AbstractByteBufAllocator;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ComponentScan(basePackages = {"ch.valtech.kubernetes.microservice.cluster.persistence",
    "ch.valtech.kubernetes.microservice.cluster.security.config"})
@ImportRuntimeHints(PersistenceApplicationRuntimeHints.class)
public class PersistenceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PersistenceApplication.class, args);
  }

}

class PersistenceApplicationRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.reflection().registerType(AbstractByteBufAllocator.class, MemberCategory.INVOKE_DECLARED_METHODS);
  }

}
