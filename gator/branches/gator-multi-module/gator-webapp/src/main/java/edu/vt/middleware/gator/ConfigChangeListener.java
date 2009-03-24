/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

/**
 * Subscriber-side interface for a publisher-subscriber pattern whereby changes
 * to log4j configuration is published to registered subscribers.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public interface ConfigChangeListener
{
  /**
   * Callback method is invoked on registered listeners whenever a log4j
   * project configuration changes.
   * @param sender Change publisher.
   * @param project Project configuration that changed.  The
   * {@link ProjectConfig#getId()} should be expected to be invariant and
   * therefore may be used to identify projects and track changes.
   */
  void projectChanged(Object sender, ProjectConfig project);


  /**
   * Callback method is invoked on registered listeners whenever a log4j
   * project configuration is removed.
   * @param sender Change publisher.
   * @param project Project configuration that was deleted.
   */
  void projectRemoved(Object sender, ProjectConfig project);
}
