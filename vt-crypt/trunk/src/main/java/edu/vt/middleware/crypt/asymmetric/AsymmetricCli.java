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
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Command line interface for asymmetric encryption operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AsymmetricCli extends AbstractEncryptionCli
{

  /** Generate key pair option. */
  protected static final String OPT_GENKEYPAIR = "genkeypair";

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
    final CommandLineParser parser = new GnuParser();
    final AsymmetricCli cli = new AsymmetricCli();
    cli.initOptions();
    try {
      if (args.length > 0) {
        final CommandLine line = parser.parse(cli.options, args);
        if (line.hasOption(OPT_ENCRYPT)) {
          cli.encrypt(line);
        } else if (line.hasOption(OPT_DECRYPT)) {
          cli.decrypt(line);
        } else if (line.hasOption(OPT_GENKEYPAIR)) {
          cli.genKeyPair(line);
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
    encrypt.setArgName("filepath");
    encrypt.setOptionalArg(false);

    final Option decrypt = new Option(
      OPT_DECRYPT,
      true,
      "decrypt with PKCS#8 DER-encoded private key");
    decrypt.setArgName("filepath");
    decrypt.setOptionalArg(false);

    options.addOption(genKeyPair);
    options.addOption(privKeyPath);
    options.addOption(encrypt);
    options.addOption(decrypt);
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
    if (!line.hasOption(OPT_OUTFILE)) {
      throw new IllegalArgumentException(
        "genkey operation requires -out for public key output path");
    }
    if (!line.hasOption(OPT_PRIVKEYPATH)) {
      throw new IllegalArgumentException(
        "genkey operation requires -privkey for private key output path");
    }

    final AsymmetricAlgorithm alg = newAlgorithm(line);
    final int size = Integer.parseInt(line.getOptionValue(OPT_GENKEYPAIR));
    System.err.println("Generating " + alg + " key pair of " + size + " bits");

    final KeyPair keyPair = alg.generateKeys(size);
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
    final String alg = line.getOptionValue(OPT_CIPHER);
    final File keyFile = new File(line.getOptionValue(OPT_ENCRYPT));
    System.err.println("Reading public key from " + keyFile);
    try {
      key = CryptReader.readPublicKey(keyFile, alg);
    } catch (CryptException e) {
      // Maybe the file is an X.509 DER-encoded cert containing the public key
      key = CryptReader.readCertificate(keyFile).getPublicKey();
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
    return
      CryptReader.readPrivateKey(
        new File(line.getOptionValue(OPT_DECRYPT)),
        line.getOptionValue(OPT_CIPHER));
  }
}
