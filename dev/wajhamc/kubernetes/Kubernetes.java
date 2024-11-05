package dev.wajhamc.kubernetes;

import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import io.lettuce.core.RedisClient; // wajhamc stack started xd
import io.lettuce.core.RedisURI;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class that contains utility methods for kubernetes.
 */
public final class Kubernetes {

  /**
   * the client.
   */
  @Nullable
  private static KubernetesClient client;

  /**
   * the redis client.
   */
  @Nullable
  private static RedisClient redisClient;

  /**
   * ctor.
   */
  private Kubernetes() {
  }

  /**
   * initiates the client.
   */
  public static void initClient() {
    Kubernetes.client = new DefaultKubernetesClient();
  }

  /**
   * initiates the redis.
   *
   * @param masterName the master name to initiate.
   * @param password the password to initiate.
   */
  public static void initRedis(@NotNull final String masterName, @NotNull final String password) {
    final var sentinel = Kubernetes.kubernetesClient().services()
      .inNamespace("redis")
      .withName("sentinel")
      .get()
      .getSpec();
    final var ip = sentinel.getClusterIP();
    final var port = sentinel.getPorts().stream()
      .findFirst()
      .map(ServicePort::getPort)
      .orElseThrow();
    Kubernetes.redisClient = RedisClient.create(RedisURI.Builder.sentinel(ip, port, masterName)
      .withPassword(password.toCharArray())
      .build());
  }

  /**
   * initiates the redis.
   *
   * @param password the password to initiate.
   */
  public static void initRedis(@NotNull final String password) {
    Kubernetes.initRedis("mymaster", password);
  }

  /**
   * initiates the redis.
   */
  public static void initRedis() {
    Kubernetes.initRedis("password");
  }

  /**
   * obtains the kubernetes client.
   *
   * @return kubernetes client.
   */
  @NotNull
  public static KubernetesClient kubernetesClient() {
    if (Kubernetes.client == null) {
      Kubernetes.initClient();
    }
    return Kubernetes.client;
  }

  /**
   * obtains the redis client.
   *
   * @return redis client.
   */
  @NotNull
  public static RedisClient redisClient() {
    return Objects.requireNonNull(Kubernetes.redisClient, "redis client");
  }
}
