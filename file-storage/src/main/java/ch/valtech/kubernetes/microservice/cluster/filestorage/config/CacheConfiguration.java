package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import static ch.valtech.kubernetes.microservice.cluster.filestorage.config.SpringProfiles.CLOUD;
import static ch.valtech.kubernetes.microservice.cluster.filestorage.config.SpringProfiles.PROD;

import ch.valtech.kubernetes.microservice.cluster.filestorage.annotation.OnProfile;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfiguration {

  @PreDestroy
  public void destroy() {
    log.info("Closing Cache Manager");
    Hazelcast.shutdownAll();
  }

  @Bean
  @OnProfile(any = {PROD, CLOUD})
  public ClientConfig clientConfig() {
    ClientConfig clientConfig = new ClientConfig();
    ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
    networkConfig.addAddress("127.0.0.1:5701")
        .setRedoOperation(true)
        .setConnectionTimeout(5000);
    return clientConfig;
  }

  @Bean
  @OnProfile(any = {PROD, CLOUD})
  public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
    return HazelcastClient.newHazelcastClient(clientConfig);
  }

  @Bean
  @OnProfile(any = {PROD, CLOUD})
  public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
    log.debug("Starting HazelcastCacheManager");
    return new HazelcastCacheManager(hazelcastInstance);
  }

  @Bean
  @Profile("!prod & !cloud")
  public CacheManager noOpCacheManager() {
    return new NoOpCacheManager();
  }

}
