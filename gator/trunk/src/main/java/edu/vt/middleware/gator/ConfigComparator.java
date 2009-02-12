/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Middleware.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

import java.util.Comparator;

/**
 * Comparator for {@link Config} objects that sorts by name, falling back to
 * ID in the case where names are equal.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class ConfigComparator implements Comparator<Config>
{
  /** {@inheritDoc} */
  public int compare(final Config a, final Config b)
  {
    final int result = a.getName().compareToIgnoreCase(b.getName());
    if (result == 0) {
      return a.getId() - b.getId();
    } else {
      return result;
    }
  }

}
