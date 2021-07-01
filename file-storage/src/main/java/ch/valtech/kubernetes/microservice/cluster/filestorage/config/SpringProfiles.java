/*
 * Copyright 2015 - 2018 Extron Electronics. All rights reserved.
 */

package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

public interface SpringProfiles {

  String CLOUD = "cloud";
  String NOT_CLOUD = "!cloud";
  String PROD = "prod";
  String NOT_PROD = "!prod";
  String RELEASE_TOGGLES = "release-toggles";

}
