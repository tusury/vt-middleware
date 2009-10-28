/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt;

import java.util.regex.Pattern;

/**
 * Utility class with helper methods for CLI unit tests.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CliHelper
{

  /** Whitespace pattern used to split command line args. */
  protected static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");


  /** Protected constructor of utilty class. */
  protected CliHelper() {}


  /**
   * Splits a command line into individual arguments using whitespace as a
   * delimiter.
   *
   * @param  commandLine  Command line to split into component arguments.
   *
   * @return  Array of arguments.
   */
  public static String[] splitArgs(final String commandLine)
  {
    return WHITESPACE_PATTERN.split(commandLine);
  }
}
