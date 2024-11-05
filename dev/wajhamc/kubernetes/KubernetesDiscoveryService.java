package dev.wajhamc.kubernetes;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * a record class that represents discovery services.
 */
public final record KubernetesDiscoveryService(
  @NotNull KubernetesClient client
) implements DiscoveryService {

  /**
   * discovers all the services.
   *
   * @return discovered services.
   */
  @NotNull
  @Override
  public Set<DiscoveredService> discover() {
    return PodMapper.backendServicesFromPodList(this.allPods());
  }

  /**
   * obtains all the pods.
   *
   * @return pods labeled {@link PodMapper#BASE_ANNOTATION}.
   */
  @NotNull
  private PodList allPods() {
    return this.client.pods()
      .inNamespace("network")
      .withLabel(PodMapper.BASE_ANNOTATION, "true")
      .list();
  }
}
