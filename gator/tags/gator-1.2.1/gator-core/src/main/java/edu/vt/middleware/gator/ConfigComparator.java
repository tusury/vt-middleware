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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for {@link Config} objects that sorts by name, falling back to ID
 * in the case where names are equal.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ConfigComparator implements Comparator<Config>, Serializable
{

  /** ConfigComparator.java. */
  private static final long serialVersionUID = 8591283570241252559L;

  /** {@inheritDoc}. */
  public int compare(final Config a, final Config b)
  {
    int result = 0;
    if (a == null) {
      if (b != null) {
        result = -1;
      }
    } else {
      if (b == null) {
        result = 1;
      } else {
        if (a.getName() == null) {
          if (b.getName() == null) {
            result = a.getId() - b.getId();
          } else {
            result = -1;
          }
        } else {
          if (b.getName() == null) {
            result = 1;
          } else {
            result = a.getName().compareToIgnoreCase(b.getName());
            if (result == 0) {
              result = a.getId() - b.getId();
            }
          }
        }
      }
    }
    return result;
  }

}
