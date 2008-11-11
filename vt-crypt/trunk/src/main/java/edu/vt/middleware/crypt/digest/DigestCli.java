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
package edu.vt.middleware.crypt.digest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import edu.vt.middleware.crypt.AbstractCli;
import edu.vt.middleware.crypt.util.Base64Converter;
import edu.vt.middleware.crypt.util.Converter;
import edu.vt.middleware.crypt.util.HexConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Command line interface for digest operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DigestCli extends AbstractCli
{

  /** Digest algorithm option. */
  protected static final String OPT_ALG = "alg";

  /** Salt for digest initialization. */
  protected static final String OPT_SALT = "salt";

  /** Input file option. */
  protected static final String OPT_INFILE = "in";

  /** Output encoding format. */
  protected static final String OPT_OUTFORM = "outform";

  /** Name of operation provided by this class. */
  private static final String COMMAND_NAME = "digest";

  /** Converts hex to bytes and vice versa. */
  private HexConverter hexConv = new HexConverter();


  /**
   * CLI entry point method.
   *
   * @param  args  Command line arguments.
   */
  public static void main(final String[] args)
  {
    final CommandLineParser parser = new GnuParser();
    final DigestCli cli = new DigestCli();
    cli.initOptions();
    try {
      if (args.length > 0) {
        cli.digest(parser.parse(cli.options, args));
      } else {
        cli.printHelp();
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


  /** {@inheritDoc} */
  protected void initOptions()
  {
    super.initOptions();

    final Option algorithm = new Option(OPT_ALG, true, "digest algorithm name");
    algorithm.setRequired(true);
    algorithm.setArgName("name");
    algorithm.setOptionalArg(false);

    final Option salt = new Option(
      OPT_SALT,
      true,
      "initialize digest with salt before hashing data");
    salt.setArgName("hex_salt");
    salt.setOptionalArg(false);

    final Option infile = new Option(
      OPT_INFILE,
      true,
      "file to digest; defaults to STDIN");
    infile.setArgName("filepath");
    infile.setOptionalArg(false);

    final Option outform = new Option(
      OPT_OUTFORM,
      true,
      "output encoding format, either hex or base64");
    outform.setArgName("encoding");
    outform.setOptionalArg(false);

    options.addOption(algorithm);
    options.addOption(salt);
    options.addOption(infile);
    options.addOption(outform);
  }


  /**
   * Compute a digest of a data stream from options defined on command line.
   *
   * @param  line  Command line argument container.
   *
   * @throws  Exception  On digest errors.
   */
  protected void digest(final CommandLine line)
    throws Exception
  {
    final DigestAlgorithm digest = DigestAlgorithm.newInstance(
      line.getOptionValue(OPT_ALG));
    if (line.hasOption(OPT_SALT)) {
      digest.setSalt(hexConv.toBytes(line.getOptionValue(OPT_SALT)));
    }

    InputStream in = null;
    if (line.hasOption(OPT_INFILE)) {
      final File file = new File(line.getOptionValue(OPT_INFILE));
      System.err.println("Reading input from " + file);
      in = new BufferedInputStream(new FileInputStream(file));
    } else {
      System.err.println("Reading input from STDIN");
      in = System.in;
    }

    final byte[] hash = digest.digest(in);
    closeStream(in);
    if (line.hasOption(OPT_OUTFORM)) {
      final String encName = line.getOptionValue(OPT_OUTFORM);
      Converter conv = null;
      if (BASE_64_ENCODING.equals(encName)) {
        conv = new Base64Converter();
      } else if (HEX_ENCODING.equals(encName)) {
        conv = hexConv;
      } else {
        throw new IllegalArgumentException("Unknown encoding.");
      }
      System.out.println(conv.fromBytes(hash));
    } else {
      // Suppress line feed since the expected use case here is chaining
      // with other tools
      System.out.print(hash);
    }
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }

}
