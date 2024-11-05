package dev.wajhamc.kubernetes;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine discovery services.
 */
public interface DiscoveryService {

  /**
   * discovers all the services.
   *
   * @return discovered services.
   */
  @NotNull
  Set<DiscoveredService> discover();
}
