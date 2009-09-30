/*
  $Id: AbstractCli.java 12 2008-11-17 19:13:16Z marvin.addison $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 12 $
  Updated: $Date: 2008-11-17 14:13:16 -0500 (Mon, 17 Nov 2008) $
*/
package edu.vt.middleware.crypt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Abstract base class for all CLI handlers.
 *
 * @author  Middleware Services
 * @version  $Revision: 12 $
 */
public abstract class AbstractCli
{

  /** Input file option. */
  protected static final String OPT_INFILE = "in";

  /** Example option */
  protected static final String OPT_EXAMPLE = "examples";

  /** Name of encoding option value for Base-64 encoding. */
  protected static final String BASE_64_ENCODING = "base64";

  /** Name of encoding option value for hexadecimal encoding. */
  protected static final String HEX_ENCODING = "hex";

  /** Suffix of files using PEM encoding. */
  protected static final String PEM_SUFFIX = "pem";

  /** Command line options. */
  protected Options options = new Options();


  /**
   * Parses command line options and invokes the proper handler to perform
   * the requested action, or the default action if no action is specified.
   *
   * @param  args  Command line arguments.
   */
  public final void performAction(final String[] args)
  {
    initOptions();
    try {
      if (args.length > 0) {
        final CommandLineParser parser = new GnuParser();
        final CommandLine line = parser.parse(options, args);
        if (line.hasOption(OPT_EXAMPLE)) {
          printExamples();
        } else {
          dispatch(line);
        }
      } else {
        printHelp();
      }
    } catch (ParseException pex) {
      System.err.println(
        "Failed parsing command arguments: " + pex.getMessage());
    } catch (IllegalArgumentException iaex) {
      String msg = "Operation failed: " + iaex.getMessage();
      if (iaex.getCause() != null) {
        msg += " Underlying reason: " + iaex.getCause().getMessage();
      }
      System.err.println(msg);
    } catch (Exception ex) {
      System.err.println("Operation failed:");
      ex.printStackTrace(System.err);
    }
  }


  /** Initialize CLI options. */
  protected void initOptions()
  {
    options.addOption(new Option(OPT_EXAMPLE, "print usage examples"));
    options.addOption(new Option("help", "print a command summary"));
  }


  /**
   * Gets the name of the command for which this class provides a CLI interface.
   *
   * @return  Name of CLI command.
   */
  protected abstract String getCommandName();


  /**
   * Dispatch command line data to the handler that can perform the
   * operation requested on the command line.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws Exception On errors thrown by handler.
   */
  protected abstract void dispatch(final CommandLine line) throws Exception;


  /** Prints CLI help text. */
  protected void printHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(getCommandName(), options);
  }


  /** Prints CLI usage examples. */
  protected void printExamples()
  {
    final String fullName = getClass().getName();
    final String name = fullName.substring(fullName.lastIndexOf('.') + 1);
    final InputStream in = getClass().getResourceAsStream(name + ".examples");
    if (in != null) {
      final BufferedReader reader = new BufferedReader(
        new InputStreamReader(in));
      try {
        System.out.println();
        String line = null;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      } catch (IOException e) {
        System.err.println("Error reading examples from resource stream.");
      } finally {
        try {
          reader.close();
        } catch (IOException ex) {
          System.err.println("Error closing example resource stream.");
        }
        System.out.println();
      }
    } else {
      System.out.println("No usage examples available for " + getCommandName());
    }
  }


  /**
   * Get an input stream containing data to be signed or verified based on CLI
   * arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  Input stream.
   *
   * @throws  IOException  On stream creation errors.
   */
  protected InputStream getInputStream(final CommandLine line)
    throws IOException
  {
    return getInputStream(line, OPT_INFILE);
  }


  /**
   * Get an input stream containing data to be signed or verified based on CLI
   * arguments.
   *
   * @param  line  Parsed command line arguments container.
   * @param  opt  Name of command line option used to specify file input stream.
   *
   * @return  Input stream.
   *
   * @throws  IOException  On stream creation errors.
   */
  protected InputStream getInputStream(final CommandLine line, final String opt)
    throws IOException
  {
    InputStream in = null;
    if (line.hasOption(opt)) {
      final File file = new File(line.getOptionValue(opt));
      System.err.println("Reading input from " + file);
      in = new BufferedInputStream(new FileInputStream(file));
    } else {
      System.err.println("Reading input from STDIN");
      in = System.in;
    }
    return in;
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
