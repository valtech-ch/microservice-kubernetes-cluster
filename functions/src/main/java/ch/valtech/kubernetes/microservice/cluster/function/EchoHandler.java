package ch.valtech.kubernetes.microservice.cluster.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

public class EchoHandler extends FunctionInvoker<String, String> {

  @FunctionName("echo")
  public String execute(@HttpTrigger(name = "req", methods = HttpMethod.POST,
      authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
      ExecutionContext context) {
    return handleRequest(request.getBody(), context);
  }

}
