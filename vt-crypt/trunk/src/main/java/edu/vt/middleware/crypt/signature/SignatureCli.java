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
package edu.vt.middleware.crypt.signature;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import edu.vt.middleware.crypt.AbstractCli;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.util.Base64Converter;
import edu.vt.middleware.crypt.util.Converter;
import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.util.HexConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Command line interface for cryptographic signature operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SignatureCli extends AbstractCli
{

  /** Signature algorithm option. */
  protected static final String OPT_ALG = "alg";

  /** Input file option. */
  protected static final String OPT_INFILE = "in";

  /** Output encoding format. */
  protected static final String OPT_ENCODING = "encoding";

  /** Message digest used to produce encoded message to sign. */
  protected static final String OPT_DIGEST = "digest";

  /** Path to key used for signing or verification. */
  protected static final String OPT_KEY = "key";

  /** Sign operation option. */
  protected static final String OPT_SIGN = "sign";

  /** Verify operation option. */
  protected static final String OPT_VERIFY = "verify";

  /** Name of operation provided by this class. */
  private static final String COMMAND_NAME = "sign";


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }


  /**
   * CLI entry point method.
   *
   * @param  args  Command line arguments.
   */
  public static void main(final String[] args)
  {
    final CommandLineParser parser = new GnuParser();
    final SignatureCli cli = new SignatureCli();
    cli.initOptions();
    try {
      if (args.length > 0) {
        final CommandLine line = parser.parse(cli.options, args);
        if (line.hasOption(OPT_SIGN)) {
          cli.sign(line);
        } else if (line.hasOption(OPT_VERIFY)) {
          cli.verify(line);
        } else {
          cli.printHelp();
        }
      } else {
        cli.printHelp();
      }
    } catch (ParseException pex) {
      System.err.println(
        "Failed parsing command arguments: " + pex.getMessage());
    } catch (IllegalArgumentException iaex) {
      String msg = "Operation failed: " + iaex.getMessage();
      if (iaex.getCause() != null) {
        msg += ". Underlying reason: " + iaex.getCause().getMessage();
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

    final Option alg = new Option(
      OPT_ALG,
      true,
      "signature algorithm; either DSA or RSA");
    alg.setRequired(true);
    alg.setArgName("name");
    alg.setOptionalArg(false);

    final Option key = new Option(
      OPT_KEY,
      true,
      "DER-encoded PKCS#8 private key for signing or " +
      "X.509 cert/public key for verification");
    key.setRequired(true);
    key.setArgName("filepath");
    key.setOptionalArg(false);

    final Option infile = new Option(
      OPT_INFILE,
      true,
      "file to sign/verify; defaults to STDIN");
    infile.setArgName("filepath");
    infile.setOptionalArg(false);

    final Option digest = new Option(
      OPT_DIGEST,
      true,
      "message digest algorithm used to produce encoded message to sign");
    digest.setArgName("algname");
    digest.setOptionalArg(false);

    final Option encoding = new Option(
      OPT_ENCODING,
      true,
      "signature encoding format, either base64 or hex");
    encoding.setArgName("format");
    encoding.setOptionalArg(false);

    final Option verify = new Option(
      OPT_VERIFY,
      true,
      "verify the given signature; signature encoding determined by -encoding");
    encoding.setArgName("signature");
    encoding.setOptionalArg(false);

    options.addOption(alg);
    options.addOption(key);
    options.addOption(infile);
    options.addOption(digest);
    options.addOption(encoding);
    options.addOption(verify);
    options.addOption(new Option(OPT_SIGN, "perform sign operation"));
  }


  /**
   * Creates a new signature algorithm instance based on command line args.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  New instance from CLI args.
   */
  protected SignatureAlgorithm newInstance(final CommandLine line)
  {
    SignatureAlgorithm sig = null;
    if (line.hasOption(OPT_DIGEST)) {
      sig = SignatureAlgorithm.newInstance(
        line.getOptionValue(OPT_ALG),
        line.getOptionValue(OPT_DIGEST));
    } else {
      sig = SignatureAlgorithm.newInstance(line.getOptionValue(OPT_ALG));
    }
    return sig;
  }


  /**
   * Perform a signature operation on input data.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On sign errors.
   */
  protected void sign(final CommandLine line)
    throws Exception
  {
    final SignatureAlgorithm sig = newInstance(line);
    final File keyFile = new File(line.getOptionValue(OPT_KEY));
    System.err.println("Reading private key from " + keyFile);
    sig.setSignKey(CryptReader.readPrivateKey(keyFile, sig.getAlgorithm()));
    sig.initSign();

    final InputStream in = getInputStream(line);
    final byte[] sigBytes = sig.sign(getInputStream(line));
    closeStream(in);
    if (line.hasOption(OPT_ENCODING)) {
      final String encName = line.getOptionValue(OPT_ENCODING);
      Converter conv = null;
      if (BASE_64_ENCODING.equals(encName)) {
        conv = new Base64Converter();
      } else if (HEX_ENCODING.equals(encName)) {
        conv = new HexConverter();
      } else {
        throw new IllegalArgumentException("Unknown encoding.");
      }
      System.out.println(conv.fromBytes(sigBytes));
    } else {
      // Suppress line feed since the expected use case here is chaining
      // with other tools
      System.out.print(sigBytes);
    }
  }


  /**
   * Perform a verification operation on input data.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On sign errors.
   */
  protected void verify(final CommandLine line)
    throws Exception
  {
    final SignatureAlgorithm sig = newInstance(line);
    sig.setVerifyKey(readPublicKey(line));
    sig.initVerify();

    final InputStream in = getInputStream(line);
    byte[] sigBytes = null;
    if (line.hasOption(OPT_ENCODING)) {
      final String encName = line.getOptionValue(OPT_ENCODING);
      Converter conv = null;
      if (BASE_64_ENCODING.equals(encName)) {
        conv = new Base64Converter();
      } else if (HEX_ENCODING.equals(encName)) {
        conv = new HexConverter();
      } else {
        throw new IllegalArgumentException("Unknown encoding.");
      }
      sigBytes = conv.toBytes(line.getOptionValue(OPT_VERIFY));
    } else {
      throw new IllegalArgumentException(
        "Encoding is required for the signature verification string");
    }

    final boolean isVerified = sig.verify(in, sigBytes);
    closeStream(in);
    if (isVerified) {
      System.out.println("SUCCESS -- signature verified.");
    } else {
      System.out.println("FAILURE -- signature does not match.");
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
    InputStream in = null;
    if (line.hasOption(OPT_INFILE)) {
      final File file = new File(line.getOptionValue(OPT_INFILE));
      System.err.println("Reading input from " + file);
      in = new BufferedInputStream(new FileInputStream(file));
    } else {
      System.err.println("Reading input from STDIN");
      in = System.in;
    }
    return in;
  }


  /**
   * Creates a public key from a file defined by CLI arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  Public key used for signature verification.
   *
   * @throws  Exception  On IO or key format errors.
   */
  protected PublicKey readPublicKey(final CommandLine line)
    throws Exception
  {
    PublicKey key = null;
    final String alg = line.getOptionValue(OPT_ALG);
    final File keyFile = new File(line.getOptionValue(OPT_KEY));
    System.err.println("Reading public key from " + keyFile);
    try {
      key = CryptReader.readPublicKey(keyFile, alg);
    } catch (CryptException e) {
      // Maybe the file is an X.509 DER-encoded cert containing the public key
      key = CryptReader.readCertificate(keyFile).getPublicKey();
    }
    return key;
  }
}
