/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.signature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import edu.vt.middleware.crypt.AbstractCli;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.io.Base64FilterInputStream;
import edu.vt.middleware.crypt.io.HexFilterInputStream;
import edu.vt.middleware.crypt.util.Base64Converter;
import edu.vt.middleware.crypt.util.Converter;
import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.util.HexConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

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


  /**
   * CLI entry point method.
   *
   * @param  args  Command line arguments.
   */
  public static void main(final String[] args)
  {
    new SignatureCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    super.initOptions();

    final Option alg = new Option(
      OPT_ALG,
      true,
      "signature algorithm; either DSA or RSA");
    alg.setArgName("name");
    alg.setOptionalArg(false);

    final Option key = new Option(
      OPT_KEY,
      true,
      "DER-encoded PKCS#8 private key for signing or " +
      "X.509 cert/public key for verification");
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
      "verify signature in given file; " +
      "signature encoding determined by -encoding option");
    encoding.setArgName("sigfilepath");
    encoding.setOptionalArg(false);

    options.addOption(alg);
    options.addOption(key);
    options.addOption(infile);
    options.addOption(digest);
    options.addOption(encoding);
    options.addOption(verify);
    options.addOption(new Option(OPT_SIGN, "perform sign operation"));
  }


  /** {@inheritDoc} */
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_SIGN)) {
      sign(line);
    } else if (line.hasOption(OPT_VERIFY)) {
      verify(line);
    } else {
      printHelp();
    }
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
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
    SignatureAlgorithm sig;
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
    validateOptions(line);

    final SignatureAlgorithm sig = newInstance(line);
    final File keyFile = new File(line.getOptionValue(OPT_KEY));
    System.err.println("Reading private key from " + keyFile);
    sig.setSignKey(readPrivateKey(line));
    sig.initSign();

    final InputStream in = getInputStream(line);
    final byte[] sigBytes = sig.sign(getInputStream(line));
    closeStream(in);
    if (line.hasOption(OPT_ENCODING)) {
      final String encName = line.getOptionValue(OPT_ENCODING);
      Converter conv;
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
    validateOptions(line);

    final InputStream in = getInputStream(line);
    final SignatureAlgorithm sig = newInstance(line);
    sig.setVerifyKey(readPublicKey(line));
    sig.initVerify();

    boolean isVerified = false;
    try {
      isVerified = sig.verify(in, readSignature(line));
    } finally {
      closeStream(in);
    }
    if (isVerified) {
      System.out.println("SUCCESS -- signature verified.");
    } else {
      System.out.println("FAILURE -- signature does not match.");
    }
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
    PublicKey key;
    final File keyFile = new File(line.getOptionValue(OPT_KEY));
    System.err.println("Reading public key from " + keyFile);
    try {
      // The commonest case is an X.509 cert containing the public key
      key = CryptReader.readCertificate(keyFile).getPublicKey();
    } catch (Exception e) {
      // Try treating file as a standalone key in X.509 format
      key = CryptReader.readPublicKey(keyFile);
    }
    return key;
  }


  /**
   * Creates a private key from a file defined by CLI arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  Private key.
   *
   * @throws  CryptException  On crypto errors.
   * @throws  IOException  On I/O errors.
   */
  protected PrivateKey readPrivateKey(final CommandLine line)
    throws CryptException, IOException
  {
    final File keyFile = new File(line.getOptionValue(OPT_KEY));
    return CryptReader.readPrivateKey(keyFile);
  }


  /**
   * Reads a cryptographic signature from a file, possibly in encoded format,
   * and returns the result as the raw signature bytes.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  Signature bytes.
   *
   * @throws  IOException  On read errors.
   */
  protected byte[] readSignature(final CommandLine line)
    throws IOException
  {
    InputStream in = getInputStream(line, OPT_VERIFY);
    if (line.hasOption(OPT_ENCODING)) {
      final String encName = line.getOptionValue(OPT_ENCODING);
      if (BASE_64_ENCODING.equals(encName)) {
        in = new Base64FilterInputStream(in);
      } else if (HEX_ENCODING.equals(encName)) {
        in = new HexFilterInputStream(in);
      } else {
        throw new IllegalArgumentException("Unknown encoding.");
      }
    }

    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final int bufSize = 1024;
    final byte[] buffer = new byte[bufSize];
    int count;
    while ((count = in.read(buffer)) > 0) {
      os.write(buffer, 0, count);
    }
    return os.toByteArray();
  }


  /**
   * Validates the existence of required options for an operation.
   *
   * @param  line  Parsed command line arguments container.
   */
  protected void validateOptions(final CommandLine line)
  {
    if (!line.hasOption(OPT_ALG)) {
      throw new IllegalArgumentException("alg option is required.");
    }
    if (!line.hasOption(OPT_KEY)) {
      throw new IllegalArgumentException("key option is required.");
    }
  }
}
