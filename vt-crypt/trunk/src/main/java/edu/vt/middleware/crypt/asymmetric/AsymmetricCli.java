/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.asymmetric;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import edu.vt.middleware.crypt.AbstractEncryptionCli;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.util.CryptWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for asymmetric encryption operations.
 *
 * @author  Middleware Services
 * @version  $Revision: 12 $
 */
public class AsymmetricCli extends AbstractEncryptionCli
{

  /** Generate key pair option. */
  protected static final String OPT_GENKEYPAIR = "genkeys";

  /** Output path of private key for keypair generation option. */
  protected static final String OPT_PRIVKEYPATH = "privkey";

  /** Name of operation provided by this class. */
  private static final String COMMAND_NAME = "pkc";


  /**
   * CLI entry point method.
   *
   * @param  args  Command line arguments.
   */
  public static void main(final String[] args)
  {
    new AsymmetricCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    super.initOptions();

    final Option genKeyPair = new Option(
      OPT_GENKEYPAIR,
      true,
      "generate key pair of size; " +
      "public key written to -out path, " +
      "private key written to -privkey path");
    genKeyPair.setArgName("bitsize");
    genKeyPair.setOptionalArg(false);

    final Option privKeyPath = new Option(
      OPT_PRIVKEYPATH,
      true,
      "output path of generated private key");
    privKeyPath.setArgName("filepath");

    final Option encrypt = new Option(
      OPT_ENCRYPT,
      true,
      "encrypt with X.509 DER-encoded public key or certificate");
    encrypt.setArgName("pubkeypath");
    encrypt.setOptionalArg(false);

    final Option decrypt = new Option(
      OPT_DECRYPT,
      true,
      "decrypt with PKCS#8 DER-encoded private key");
    decrypt.setArgName("privkeypath");
    decrypt.setOptionalArg(false);

    options.addOption(genKeyPair);
    options.addOption(privKeyPath);
    options.addOption(encrypt);
    options.addOption(decrypt);
  }


  /** {@inheritDoc} */
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_ENCRYPT)) {
      encrypt(line);
    } else if (line.hasOption(OPT_DECRYPT)) {
      decrypt(line);
    } else if (line.hasOption(OPT_GENKEYPAIR)) {
      genKeyPair(line);
    } else {
      printHelp();
    }
  }


  /**
   * Creates a new asymmetric encryption algorithm instance based on CLI
   * options.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  New instance of an initialized asymmetric algorithm.
   */
  protected AsymmetricAlgorithm newAlgorithm(final CommandLine line)
  {
    if (!line.hasOption(OPT_CIPHER)) {
      throw new IllegalArgumentException("cipher is required.");
    }
    return AsymmetricAlgorithm.newInstance(line.getOptionValue(OPT_CIPHER));
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }


  /**
   * Perform an encryption operation using data specified on the command line.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On encryption errors.
   */
  protected void encrypt(final CommandLine line)
    throws Exception
  {
    validateOptions(line);

    final AsymmetricAlgorithm alg = newAlgorithm(line);
    alg.setKey(readPublicKey(line));
    encrypt(alg, getInputStream(line), getOutputStream(line));
  }


  /**
   * Perform a decryption operation using data specified on the command line.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On encryption errors.
   */
  protected void decrypt(final CommandLine line)
    throws Exception
  {
    validateOptions(line);

    final AsymmetricAlgorithm alg = newAlgorithm(line);
    alg.setKey(readPrivateKey(line));
    decrypt(alg, getInputStream(line), getOutputStream(line));
  }


  /**
   * Generate a new encryption public/private key pair using CLI arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On encryption errors.
   */
  protected void genKeyPair(final CommandLine line)
    throws Exception
  {
    validateOptions(line);

    final AsymmetricAlgorithm alg = newAlgorithm(line);
    final int size = Integer.parseInt(line.getOptionValue(OPT_GENKEYPAIR));
    System.err.println("Generating " + alg + " key pair of " + size + " bits");

    final KeyPair keyPair = PublicKeyUtils.generate(alg.getAlgorithm(), size);
    final File pubKeyFile = new File(line.getOptionValue(OPT_OUTFILE));
    final File privKeyFile = new File(line.getOptionValue(OPT_PRIVKEYPATH));
    CryptWriter.writeEncodedKey(keyPair.getPublic(), pubKeyFile);
    System.err.println("Wrote X.509 DER-encoded public key to " + pubKeyFile);
    CryptWriter.writeEncodedKey(keyPair.getPrivate(), privKeyFile);
    System.err.println(
      "Wrote PKCS#8 DER-encoded private key to " + privKeyFile);
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
    final File keyFile = new File(line.getOptionValue(OPT_ENCRYPT));
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
    final File keyFile = new File(line.getOptionValue(OPT_DECRYPT));
    return CryptReader.readPrivateKey(keyFile);
  }


  /**
   * Validates the existence of required options for an operation.
   *
   * @param  line  Parsed command line arguments container.
   */
  protected void validateOptions(final CommandLine line)
  {
    if (!line.hasOption(OPT_CIPHER)) {
      throw new IllegalArgumentException("cipher option is required.");
    }
    if (line.hasOption(OPT_GENKEYPAIR)) {
      if (!line.hasOption(OPT_OUTFILE)) {
        throw new IllegalArgumentException(
          "genkeys operation requires -out for public key output path");
      }
      if (!line.hasOption(OPT_PRIVKEYPATH)) {
        throw new IllegalArgumentException(
          "genkeys operation requires -privkey for private key output path");
      }
    }
  }
}
