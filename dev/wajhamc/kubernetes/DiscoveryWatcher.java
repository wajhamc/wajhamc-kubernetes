package dev.wajhamc.kubernetes;

import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine discovery watchers.
 */
public interface DiscoveryWatcher {

  /**
   * runs when a service created.
   *
   * @param service the service to run.
   */
  void onCreate(@NotNull DiscoveredService service);

  /**
   * runs when a service deleted.
   *
   * @param service the service to run.
   */
  void onDelete(@NotNull DiscoveredService service);
}
