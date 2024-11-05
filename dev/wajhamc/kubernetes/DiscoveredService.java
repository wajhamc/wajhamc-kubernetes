package dev.wajhamc.kubernetes;

import org.jetbrains.annotations.NotNull;

/**
 * a record class that represents discovered services.
 *
 * @param name the name.
 * @param host the host.
 * @param port the port.
 * @param isDefault the is default.
 */
record DiscoveredService(
  @NotNull String name,
  @NotNull String host,
  int port,
  boolean isDefault
) {

  /**
   * compact ctor.
   *
   * @param name the name.
   * @param host the host.
   * @param port the port.
   * @param isDefault the is default.
   */
  public DiscoveredService {
    name = name.isEmpty() ? "%s:%d".formatted(host, port) : name;
  }
}
