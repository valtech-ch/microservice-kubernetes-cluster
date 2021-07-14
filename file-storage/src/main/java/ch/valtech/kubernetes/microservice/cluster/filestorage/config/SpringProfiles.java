/*
 * Copyright 2015 - 2018 Extron Electronics. All rights reserved.
 */

package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

public final class SpringProfiles {

  public static final String CLOUD = "cloud";
  public static final String NOT_CLOUD = "!cloud";
  public static final String PROD = "prod";
  public static final String NOT_PROD = "!prod";
  public static final String RELEASE_TOGGLES = "release-toggles";

  private SpringProfiles() {
  }
}
