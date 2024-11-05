package dev.wajhamc.kubernetes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents server registries
 */
public final class ServerRegistry {

  /**
   * the registered default servers.
   */
  private static final Set<DiscoveredService> REGISTERED_DEFAULT_SERVERS = new HashSet<>();

  /**
   * the registered servers.
   */
  private static final Set<DiscoveredService> REGISTERED_SERVERS = new HashSet<>();

  /**
   * ctor.
   */
  private ServerRegistry() {
  }

  /**
   * registers the server.
   *
   * @param server the server to register.
   */
  public static void register(@NotNull final DiscoveredService server) {
    ServerRegistry.REGISTERED_SERVERS.add(server);
    if (server.isDefault()) {
      ServerRegistry.REGISTERED_DEFAULT_SERVERS.add(server);
    }
  }

  /**
   * obtains the registered default servers.
   *
   * @return default servers.
   */
  @NotNull
  public static Set<DiscoveredService> registeredDefaultServices() {
    return Collections.unmodifiableSet(ServerRegistry.REGISTERED_DEFAULT_SERVERS);
  }

  /**
   * obtains the registered servers.
   *
   * @return registered servers.
   */
  @NotNull
  public static Set<DiscoveredService> registeredServices() {
    return Collections.unmodifiableSet(ServerRegistry.REGISTERED_SERVERS);
  }

  /**
   * unregisters the server.
   *
   * @param server the server to unregister.
   */
  public static void unregister(@NotNull final DiscoveredService server) {
    ServerRegistry.REGISTERED_SERVERS.remove(server);
    ServerRegistry.REGISTERED_DEFAULT_SERVERS.remove(server);
  }
}
