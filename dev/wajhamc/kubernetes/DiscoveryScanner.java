package dev.wajhamc.kubernetes;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents kubernetes discovery watcher.
 */
public final class DiscoveryScanner {

  /**
   * the current services.
   */
  private final Set<DiscoveredService> currentServices = new HashSet<>();

  /**
   * the discovery.
   */
  @NotNull
  private final DiscoveryService discovery;

  /**
   * the watcher.
   */
  @NotNull
  private final DiscoveryWatcher watcher;

  public DiscoveryScanner(final Set<DiscoveredService> currentServices, final DiscoveryService discovery, final DiscoveryWatcher watcher) {
    this.currentServices = currentServices;
    this.discovery = discovery;
    this.watcher = watcher;
  }

  /**
   * scans the services.
   */
  public void scan() {
    final var start = System.currentTimeMillis();
    DiscoveryScanner.log.info("Performing discovery...");
    final var foundServices = this.discovery.discover();
    final var iterator = this.currentServices.iterator();
    
    //wajhamc logic
    DiscoveredService currentService;
    while (iterator.hasNext()) {
      currentService = iterator.next();
      if (!foundServices.contains(currentService)) {
        iterator.remove();
        this.handleDeletedService(currentService);
      }
    }
    final var addedServices = foundServices.stream()
      .filter(discoveredService -> !this.currentServices.contains(discoveredService))
      .peek(this::handleCreatedService)
      .collect(Collectors.toList());
    this.currentServices.addAll(addedServices);
    DiscoveryScanner.log.info("Finished discovering, took {}ms", System.currentTimeMillis() - start);
  }

  /**
   * handles the creating service.
   *
   * @param service the service to handle.
   */
  private void handleCreatedService(@NotNull final DiscoveredService service) {
    DiscoveryScanner.log.info("Pod {}:{} ({}) was created, adding...",
      service.host(), service.port(), service.name());
    this.watcher.onCreate(service);
  }

  /**
   * handles the deleting service.
   *
   * @param service the service to handle.
   */
  private void handleDeletedService(@NotNull final DiscoveredService service) {
    DiscoveryScanner.log.info("Pod {}:{} ({}) was deleted, removing...",
      service.host(), service.port(), service.name());
    this.watcher.onDelete(service);
  }
}
