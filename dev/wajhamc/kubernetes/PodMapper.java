package dev.wajhamc.kubernetes;

import com.google.common.base.Preconditions;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents pod mapper.
 */
public final class PodMapper {

  /**
   * the base annotation.
   */
  public static final String BASE_ANNOTATION = SystemUtils.getEnvironmentVariable(
    "MC_GAMEPLAY", "network.wajhamc.dev/enabled");

  /**
   * the default annotation.
   */
  public static final String DEFAULT_ANNOTATION = SystemUtils.getEnvironmentVariable(
    "MC_LABEL", "network.wajhamc.dev/default");

  /**
   * gets backend services from the pod list.
   *
   * @param podList the pod list to get.
   *
   * @return backend services.
   */
  @NotNull
  public static Set<DiscoveredService> backendServicesFromPodList(@NotNull final PodList podList) {
    return podList.getItems().stream()
      .filter(PodMapper::isReady)
      .map(PodMapper::tryGetPodAsDiscoveredService)
      .flatMap(Optional::stream)
      .collect(Collectors.toSet());
  }

  /**
   * gets the applicable backend port from container port.
   *
   * @param port the port to get.
   *
   * @return applicable backend port.
   */
  private static int applicableBackendPortFromContainerPort(@NotNull final ContainerPort port) {
    return port.getName() == null ? 0 : port.getContainerPort();
  }

  /**
   * gets the backend port from the pod.
   *
   * @param pod the pod to get.
   *
   * @return backend port.
   */
  private static int backendPortFromPod(@NotNull final Pod pod) {
    final var ports = pod.getSpec().getContainers().stream()
      .flatMap(c -> c.getPorts().stream())
      .map(PodMapper::applicableBackendPortFromContainerPort)
      .filter(p -> p != 0)
      .collect(Collectors.toList());
    
    Preconditions.checkState(ports.size() == 1,
      "Could not find applicable container ports for pod %s in namespace %s. Make sure the pod has either a port named 'minecraft', or a container port on 25565.",
      pod.getMetadata().getName(), pod.getMetadata().getNamespace());
    return ports.get(0);
  }

  /**
   * whether pod is ready or not.
   *
   * @param pod the pod to check.
   *
   * @return {@code true} if the pod is ready.
   */
  private static boolean isReady(@NotNull final Pod pod) {
    return pod.getStatus().getConditions().stream()
      .filter(condition -> !condition.getType().equalsIgnoreCase("ready"))
      .allMatch(condition -> condition.getStatus().equalsIgnoreCase("true"));
  }

  /**
   * gets pod as discovered service.
   *
   * @param pod the pod to get.
   *
   * @return discovered service.
   */
  @NotNull
  private static DiscoveredService podAsDiscoveredService(@NotNull final Pod pod) {
    final var metadata = pod.getMetadata();
    return new DiscoveredService(
      metadata.getName(),
      pod.getStatus().getPodIP(),
      
      PodMapper.backendPortFromPod(pod),
      metadata.getLabels() != null && metadata.getLabels().containsKey(PodMapper.DEFAULT_ANNOTATION));
  }

  /**
   * tries to get a pod as discovered service.
   *
   * @param pod the pod to get.
   *
   * @return discovered service.
   */
  @NotNull
  private static Optional<DiscoveredService> tryGetPodAsDiscoveredService(@NotNull final Pod pod) {
    try {
      return Optional.of(PodMapper.podAsDiscoveredService(pod));
    } catch (final Exception e) {
      PodMapper.log.error(e.getMessage());
      return Optional.empty();
    }
  }
}
