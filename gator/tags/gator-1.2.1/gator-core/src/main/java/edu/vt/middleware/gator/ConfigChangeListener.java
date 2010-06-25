/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

/**
 * Subscriber-side interface for a publisher-subscriber pattern whereby changes
 * to log4j configuration is published to registered subscribers.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ConfigChangeListener
{

  /**
   * Callback method is invoked on registered listeners whenever a log4j project
   * configuration changes.
   *
   * @param  sender  Change publisher.
   * @param  project  Project configuration that changed. The {@link
   * ProjectConfig#getId()} should be expected to be invariant and therefore may
   * be used to identify projects and track changes.
   */
  void projectChanged(Object sender, ProjectConfig project);


  /**
   * Callback method is invoked on registered listeners whenever a log4j project
   * configuration is removed.
   *
   * @param  sender  Change publisher.
   * @param  project  Project configuration that was deleted.
   */
  void projectRemoved(Object sender, ProjectConfig project);


  /**
   * Callback method is invoked on registered listeners whenever a client is
   * removed from a project.
   *
   * @param  sender  Change publisher.
   * @param  project  Project configuration that formerly contained the client.
   * @param  clientName  The name of the removed client.
   */
  void clientRemoved(Object sender, ProjectConfig project, String clientName);
}
