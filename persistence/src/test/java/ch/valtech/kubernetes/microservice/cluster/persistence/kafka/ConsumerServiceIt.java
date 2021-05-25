package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.valtech.kubernetes.microservice.cluster.persistence.AbstractIt;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsumerServiceIt extends AbstractIt {

  public static final String FILENAME = "test.txt";
  private static final String TOPIC = "auditing";

  private ConsumerService consumerService;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Mock
  private PersistenceService persistenceService;

  @Mock
  private AuthenticationManager authenticationManager;

  @BeforeAll
  void setUp() {
    consumerService = new ConsumerService(persistenceService, authenticationManager);
  }

  @Test
  public void shouldCheckMessageIsProduced() {
    //given
    String username = "username";
    AuditingRequestDto auditingRequestDto = AuditingRequestDto.builder()
        .filename(FILENAME)
        .action(Action.UPLOAD)
        .build();
    when(persistenceService.saveNewMessage(eq(auditingRequestDto), eq(username)))
        .thenReturn(Mono.just(MessageDto.builder().build()));
    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(createToken(username));
    when(authenticationManager.authenticate(any())).thenReturn(jwtAuthenticationToken);

    Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
    Producer<String, String> producer = new DefaultKafkaProducerFactory<String, String>(configs)
        .createProducer();

    producer.send(new ProducerRecord<>(TOPIC, null, "{\"filename\":\"test.txt\",\"action\":\"UPLOAD\"}"));
    producer.flush();

    //when
    consumerService.consumeTopic(auditingRequestDto, jwtAuthenticationToken.getToken().getTokenValue());

    //then
    verify(persistenceService, times(1)).saveNewMessage(auditingRequestDto, username);
  }

  public static Jwt createToken(String username) {
    String token = Jwts.builder()
        .setSubject(username)
        .setExpiration(new Date(System.currentTimeMillis() + 50))
        .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encode("testKey".getBytes(StandardCharsets.UTF_8)))
        .compact();
    return Jwt.withTokenValue(token)
        .header("typ", "JWT")
        .claim("test", "test")
        .subject(username)
        .build();
  }

}
