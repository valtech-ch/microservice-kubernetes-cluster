package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ContainerAwareErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.DeserializationException;

@Slf4j
final class KafkaErrorHandler implements ContainerAwareErrorHandler {

  @SneakyThrows
  @Override
  public void handle(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer,
      MessageListenerContainer container) {
    doSeeks(records, consumer);
    if (!records.isEmpty()) {
      ConsumerRecord<?, ?> consumerRecord = records.get(0);
      String topic = consumerRecord.topic();
      long offset = consumerRecord.offset();
      int partition = consumerRecord.partition();
      if (thrownException instanceof DeserializationException) {
        DeserializationException exception = (DeserializationException) thrownException;
        String malformedMessage = new String(exception.getData());
        log.error("Skipping message with topic {} and offset {} - malformed message: {} , exception: {}", topic, offset,
            malformedMessage, exception.getLocalizedMessage());
      } else {
        log.error("Skipping message with topic {} - offset {} - partition {} - exception {}", topic, offset, partition,
            thrownException);
      }
    } else {
      log.error("Consumer exception - cause: {}", thrownException.getMessage());
    }
  }

  private void doSeeks(List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer) {
    Map<TopicPartition, Long> partitions = new LinkedHashMap<>();
    AtomicBoolean first = new AtomicBoolean(true);
    records.forEach(consumerRecord -> {
      if (first.get()) {
        partitions.put(new TopicPartition(consumerRecord.topic(),
            consumerRecord.partition()), consumerRecord.offset() + 1);
      } else {
        partitions.computeIfAbsent(new TopicPartition(consumerRecord.topic(),
            consumerRecord.partition()), offset -> consumerRecord.offset());
      }
      first.set(false);
    });
    partitions.forEach(consumer::seek);
  }
}
