package edu.vt.middleware.gator.log4j;

import java.net.InetAddress;

import org.apache.log4j.spi.LoggerRepository;

import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Interface for configuring a log4j logger repository using a
 * {@link ProjectConfig} that describes configuration.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public interface Configurator
{
  /**
   * Sets the configuration manager.
   * @param manager Configuration manager.
   */
  void setConfigManager(final ConfigManager manager);

  /**
   * Sets the root directory where client logs will be written on the
   * server.  A directory for each project will be created below the root.
   * @param dir Client log root directory.
   */
  void setClientRootLogDirectory(final String dir);

  /**
   * Configures a given logger repository based on the IP address of the
   * given host.
   *
   * @param addr IP address of host to configure.
   * @param repository Logger repository to configure.
   *
   * @throws UnknownClientException On attempting to configure a client that
   * is not registered with any projects.
   * @throws ConfigurationException On configuration errors.
   */
  void configure(
    final InetAddress addr,
    final LoggerRepository repository)
    throws UnknownClientException, ConfigurationException;

  /**
   * Configures a given logger repository based a configuration project.
   *
   * @param project Project configuration.
   * @param repository Logger repository to configure.
   *
   * @throws ConfigurationException On configuration errors.
   */
  void configure(
    final ProjectConfig project,
    final LoggerRepository repository)
    throws ConfigurationException;

}