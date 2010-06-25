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
package edu.vt.middleware.gator.util;

import java.io.File;

/**
 * Utility class with public static methods for file operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public final class FileHelper
{

  /** Protected default constructor of utility class. */
  protected FileHelper() {}


  /**
   * Concatenates a series of path elements into a single path representation
   * where each part is separated by a single path separator character.
   *
   * @param  pathParts  Path elements.
   *
   * @return  Filesystem path.
   */
  public static String pathCat(final String... pathParts)
  {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pathParts.length; i++) {
      final String part = pathParts[i];
      if (part != null) {
        if (i > 0) {
          if (!part.startsWith(File.separator)) {
            sb.append(File.separatorChar);
          }
        }
        if (i + 1 < pathParts.length && part.endsWith(File.separator)) {
          sb.append(part.substring(0, part.length() - 1));
        } else {
          sb.append(part);
        }
      }
    }
    return sb.toString();
  }
}
