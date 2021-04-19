package ch.valtech.kubernetes.microservice.cluster.function;

import com.microsoft.azure.functions.ExecutionContext;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

@SpringBootApplication
public class Config {

  public static void main(String[] args) {
    SpringApplication.run(Config.class, args);
  }

  @Bean
  public Function<Message<String>, String> echo() {
    return Message::getPayload;
  }

  @Bean
  public Function<Message<Optional<String>>, String> reverse() {
    return message -> {
      if (message.getPayload().isEmpty()) {
        return "nothing";
      }
      String value = message.getPayload().get();
      ExecutionContext context = (ExecutionContext) message.getHeaders().get("executionContext");
      try {
        String reversed = StringUtils.reverse(value);
        if (context != null) {
          context.getLogger().info(String.format("Function: %s is reversing %s", context.getFunctionName(), value));
        }
        return reversed;
      } catch (Exception e) {
        if (context != null) {
          context.getLogger().severe(e.getMessage());
          context.getLogger().severe("Function could not reverse incoming request");
        }
        return ("Function error: - bad request");
      }
    };
  }

}
