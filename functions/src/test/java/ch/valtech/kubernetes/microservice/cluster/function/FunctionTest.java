package ch.valtech.kubernetes.microservice.cluster.function;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.azure.functions.ExecutionContext;
import java.util.Optional;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

public class FunctionTest {

  @Test
  void echo() {
    FunctionInvoker<String, String> functionInvoker = new FunctionInvoker(Config.class);
    ExecutionContext ec = getExecutionContext("echo");
    String result = functionInvoker.handleRequest("hello", ec);
    functionInvoker.close();
    assertThat(result).isEqualTo("hello");
  }

  @Test
  void reverse() {
    FunctionInvoker<Optional<String>, String> functionInvoker = new FunctionInvoker(Config.class);
    ExecutionContext ec = getExecutionContext("reverse");

    String result = functionInvoker.handleRequest(Optional.of("lukas"), ec);
    functionInvoker.close();
    assertThat(result).isEqualTo("sakul");
  }

  @Test
  void reverseEmpty() {
    FunctionInvoker<Optional<String>, String> functionInvoker = new FunctionInvoker(Config.class);
    ExecutionContext ec = getExecutionContext("reverse");

    String result = functionInvoker.handleRequest(Optional.empty(), ec);
    functionInvoker.close();
    assertThat(result).isEqualTo("nothing");
  }

  private ExecutionContext getExecutionContext(String functionName) {
    return new ExecutionContext() {
      @Override
      public Logger getLogger() {
        return Logger.getAnonymousLogger();
      }

      @Override
      public String getInvocationId() {
        return "invocationId";
      }

      @Override
      public String getFunctionName() {
        return functionName;
      }
    };
  }

}
