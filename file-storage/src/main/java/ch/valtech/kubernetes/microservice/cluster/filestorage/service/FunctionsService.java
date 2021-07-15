package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

public interface FunctionsService {

  String echo(String body);

  String reverse(String body);

}
