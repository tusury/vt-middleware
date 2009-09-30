/*
  $Id: CliHelper.java 318 2009-07-06 17:30:01Z marvin.addison $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 318 $
  Updated: $Date: 2009-07-06 13:30:01 -0400 (Mon, 06 Jul 2009) $
*/
package edu.vt.middleware.crypt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class with helper methods for CLI unit tests.
 *
 * @author  Middleware Services
 * @version  $Revision: 318 $
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


  /**
   * Combines one or more {@link OptionData} objects into a String array that
   * would be created from parsing a command line containing those options.
   * Any options that are null are ignored.
   *
   * @param  options  One or more command line options.
   *
   * @return  Array of arguments.
   */
  public static String[] toArgs(final OptionData ... options)
  {
    final List<String> argList = new ArrayList<String>();
    for (OptionData option : options) {
      if (option != null) {
        argList.add("-" + option.getOption());
        if (option.getArgument() != null) {
          argList.add(option.getArgument());
        }
      }
    }
    final String[] args = new String[argList.size()];
    argList.toArray(args);
    return args;
  }


  /**
   * Combines one or more {@link OptionData} objects into a command line that,
   * when parsed, would produce the the given options.
   * Any options that are null are ignored.
   *
   * @param  options  One or more command line options.
   *
   * @return  Composite command line built from given options.
   */
  public static String toCommandLine(final OptionData ... options)
  {
    final StringBuilder sb = new StringBuilder();
    int i = 0;
    for (String arg : toArgs(options)) {
      if (i++ > 0) {
        sb.append(' ');
      }
      sb.append(arg);
    }
    return sb.toString();
  }
}
