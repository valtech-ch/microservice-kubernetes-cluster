package ch.valtech.kubernetes.microservice.cluster.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

public class ReverseHandler extends FunctionInvoker<Optional<String>, String> {

  @FunctionName("reverse")
  public String execute(@HttpTrigger(name = "req", methods = {HttpMethod.GET,
      HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
      ExecutionContext context) {
    return handleRequest(request.getBody(), context);
  }

}
