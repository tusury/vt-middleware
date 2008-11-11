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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Abstract base class for all CLI handlers.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractCli
{

  /** Name of encoding option value for Base-64 encoding. */
  protected static final String BASE_64_ENCODING = "base64";

  /** Name of encoding option value for hexadecimal encoding. */
  protected static final String HEX_ENCODING = "hex";

  /** Command line options. */
  protected Options options = new Options();


  /** Initialize CLI options. */
  protected void initOptions()
  {
    options.addOption(new Option("help", "print a command summary"));
  }


  /**
   * Gets the name of the command for which this class provides a CLI interface.
   *
   * @return  Name of CLI command.
   */
  protected abstract String getCommandName();


  /** Prints CLI help text. */
  protected void printHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(getCommandName(), options);
  }


  /**
   * Attempts to close the given input stream.
   *
   * @param  in  Input stream to close.
   */
  protected void closeStream(final InputStream in)
  {
    try {
      if (in != System.in) {
        in.close();
      }
    } catch (IOException ioex) {
      System.err.println("Error closing input stream.");
    }
  }


  /**
   * Attempts to close the given output stream.
   *
   * @param  out  output stream to close.
   */
  protected void closeStream(final OutputStream out)
  {
    try {
      out.flush();
      if (out != System.out) {
        out.close();
      }
    } catch (IOException ioex) {
      System.err.println("Error closing output stream.");
    }
  }
}
