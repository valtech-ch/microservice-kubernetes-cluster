package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest.example;

import static ch.valtech.kubernetes.microservice.cluster.filestorage.config.example.ExampleReleaseToggles.TEST_123;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.annotation.aspect.ReleaseToggle;
import ch.valtech.kubernetes.microservice.cluster.releasetoggle.exception.ReleaseToggleNotEnabledException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/example")
class ExampleReleaseToggleController {

  @RequestMapping("/statement")
  public ResponseEntity<?> statement() {
    if (TEST_123.isActive()) {
      return ResponseEntity.ok().body("TEST_123 is enabled.");
    }
    throw new ReleaseToggleNotEnabledException(TEST_123.name());
  }

  @RequestMapping("/aspect")
  @ReleaseToggle("TEST_123")
  public ResponseEntity<?> aspect() {
    return ResponseEntity.ok().body("Example works!");
  }

}
