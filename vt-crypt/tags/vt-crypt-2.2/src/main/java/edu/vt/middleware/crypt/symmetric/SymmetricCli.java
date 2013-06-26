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
package edu.vt.middleware.crypt.symmetric;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import edu.vt.middleware.crypt.AbstractEncryptionCli;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.pbe.EncryptionScheme;
import edu.vt.middleware.crypt.pbe.KeyGenerator;
import edu.vt.middleware.crypt.pbe.OpenSSLEncryptionScheme;
import edu.vt.middleware.crypt.pbe.OpenSSLKeyGenerator;
import edu.vt.middleware.crypt.pbe.PBES1EncryptionScheme;
import edu.vt.middleware.crypt.pbe.PBES2EncryptionScheme;
import edu.vt.middleware.crypt.pbe.PBKDF1KeyGenerator;
import edu.vt.middleware.crypt.pbe.PBKDF2KeyGenerator;
import edu.vt.middleware.crypt.pbe.PKCS12EncryptionScheme;
import edu.vt.middleware.crypt.pbe.PKCS12KeyGenerator;
import edu.vt.middleware.crypt.pkcs.PBEParameter;
import edu.vt.middleware.crypt.pkcs.PBKDF2Parameters;
import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.util.CryptWriter;
import edu.vt.middleware.crypt.util.HexConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for symmetric encryption operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SymmetricCli extends AbstractEncryptionCli
{

  /** Cipher mode option. */
  protected static final String OPT_MODE = "mode";

  /** Cipher padding option. */
  protected static final String OPT_PADDING = "padding";

  /** Cipher initialization vector option. */
  protected static final String OPT_IV = "iv";

  /** Cipher key option. */
  protected static final String OPT_KEY = "key";

  /** Cipher key size. */
  protected static final String OPT_KEYSIZE = "keysize";

  /** Trigger password-based encryption key generation. */
  protected static final String OPT_PBE = "pbe";

  /** PBE key generation strategy, e.g. PKCS5S1 */
  protected static final String OPT_SCHEME = "scheme";

  /** Digest algorithm used with PBE modes that allow it. */
  protected static final String OPT_DIGEST = "digest";

  /** Salt for PBE key generation. */
  protected static final String OPT_SALT = "salt";

  /** Iteration count for PBE key generation. */
  protected static final String OPT_ITERATIONS = "iter";

  /** Generate key option. */
  protected static final String OPT_GENKEY = "genkey";

  /** Name of operation provided by this class. */
  private static final String COMMAND_NAME = "enc";


  /** Converts hex to bytes and vice versa. */
  private final HexConverter hexConv = new HexConverter();


  /**
   * CLI entry point method.
   *
   * @param  args  Command line arguments.
   */
  public static void main(final String[] args)
  {
    new SymmetricCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    super.initOptions();

    final Option mode = new Option(OPT_MODE, true, "cipher mode, e.g. CBC");
    mode.setArgName("name");
    mode.setOptionalArg(false);

    final Option padding = new Option(
      OPT_PADDING,
      true,
      "cipher padding strategy, e.g. PKCS5Padding");
    padding.setArgName("padding");
    padding.setOptionalArg(false);

    final Option key = new Option(OPT_KEY, true, "encryption/decryption key");
    key.setArgName("filepath");
    key.setOptionalArg(false);

    final Option keySize = new Option(
      OPT_KEYSIZE,
      true,
      "key size in bits; only needed if -key option is not specified");
    keySize.setArgName("bits");
    keySize.setOptionalArg(false);

    final Option iv = new Option(OPT_IV, true, "initialization vectory in hex");
    iv.setArgName("hex_iv");
    iv.setOptionalArg(false);

    final Option pbe = new Option(
      OPT_PBE,
      true,
      "generate PBE key from password/phrase; uses pkcs5s2 by default");
    pbe.setArgName("password");
    pbe.setOptionalArg(false);

    final Option pbeScheme = new Option(
      OPT_SCHEME,
      true,
      "PBE key generation mode; one of pkcs5s1, pkcs5s2, openssl, pkcs12");
    pbeScheme.setArgName("name");
    pbeScheme.setOptionalArg(false);

    final Option pbeDigest = new Option(
      OPT_DIGEST,
      true,
      "digest algorithm to use with PBE mode pkcs5s1 or pkcs12");
    pbeDigest.setArgName("name");
    pbeDigest.setOptionalArg(false);

    final Option salt = new Option(
      OPT_SALT,
      true,
      "salt for PBE key generation in hex");
    salt.setArgName("hex_salt");
    salt.setOptionalArg(false);

    final Option iterations = new Option(
      OPT_ITERATIONS,
      true,
      "iteration count for PBE key generation");
    salt.setArgName("count");
    salt.setOptionalArg(false);

    final Option genKey = new Option(
      OPT_GENKEY,
      true,
      "generate key of given size written to path specified by -out option");
    genKey.setArgName("bitsize");
    genKey.setOptionalArg(false);

    options.addOption(mode);
    options.addOption(padding);
    options.addOption(key);
    options.addOption(keySize);
    options.addOption(iv);
    options.addOption(pbe);
    options.addOption(pbeScheme);
    options.addOption(pbeDigest);
    options.addOption(salt);
    options.addOption(iterations);
    options.addOption(genKey);
    options.addOption(new Option(OPT_ENCRYPT, "perform encryption"));
    options.addOption(new Option(OPT_DECRYPT, "perform decryption"));
  }


  /** {@inheritDoc} */
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_ENCRYPT)) {
      encrypt(line);
    } else if (line.hasOption(OPT_DECRYPT)) {
      decrypt(line);
    } else if (line.hasOption(OPT_GENKEY)) {
      genKey(line);
    } else {
      printHelp();
    }
  }


  /**
   * Creates a new symmetric encryption algorithm instance based on CLI options.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  New instance of an initialized symmetric algorithm.
   */
  protected SymmetricAlgorithm newAlgorithm(final CommandLine line)
  {
    final String algName = line.getOptionValue(OPT_CIPHER);
    SymmetricAlgorithm algorithm;
    if (line.hasOption(OPT_MODE)) {
      if (line.hasOption(OPT_PADDING)) {
        algorithm = SymmetricAlgorithm.newInstance(
          algName,
          line.getOptionValue(OPT_MODE),
          line.getOptionValue(OPT_PADDING));
      } else {
        algorithm = SymmetricAlgorithm.newInstance(
          algName,
          line.getOptionValue(OPT_MODE),
          SymmetricAlgorithm.DEFAULT_PADDING);
      }
    } else if (line.hasOption(OPT_PADDING)) {
      algorithm = SymmetricAlgorithm.newInstance(
        algName,
        SymmetricAlgorithm.DEFAULT_MODE,
        line.getOptionValue(OPT_PADDING));
    } else {
      algorithm = SymmetricAlgorithm.newInstance(algName);
    }
    return algorithm;
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

    final SymmetricAlgorithm alg = newAlgorithm(line);
    if (line.hasOption(OPT_KEY)) {
      alg.setKey(readKey(line));
      if (line.hasOption(OPT_IV)) {
        alg.setIV(hexConv.toBytes(line.getOptionValue(OPT_IV)));
      }
      encrypt(alg, getInputStream(line), getOutputStream(line));
    } else if (line.hasOption(OPT_PBE)) {
      final InputStream in = getInputStream(line);
      final OutputStream out = getOutputStream(line);
      try {
        getPBEScheme(alg, line).encrypt(
          line.getOptionValue(OPT_PBE).toCharArray(),
          in,
          out);
      } finally {
        closeStream(in);
        closeStream(out);
      }
    } else {
      throw new IllegalArgumentException(
        "Either -key or -pbe is required for encryption or decryption.");
    }
  }


  /**
   * Perform a decryption operation using data specified on the command line.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On decryption errors.
   */
  protected void decrypt(final CommandLine line)
    throws Exception
  {
    validateOptions(line);

    final SymmetricAlgorithm alg = newAlgorithm(line);
    if (line.hasOption(OPT_KEY)) {
      alg.setKey(readKey(line));
      if (line.hasOption(OPT_IV)) {
        alg.setIV(hexConv.toBytes(line.getOptionValue(OPT_IV)));
      }
      decrypt(alg, getInputStream(line), getOutputStream(line));
    } else if (line.hasOption(OPT_PBE)) {
      final InputStream in = getInputStream(line);
      final OutputStream out = getOutputStream(line);
      try {
        getPBEScheme(alg, line).decrypt(
          line.getOptionValue(OPT_PBE).toCharArray(),
          in,
          out);
      } finally {
        closeStream(in);
        closeStream(out);
      }
    } else {
      throw new IllegalArgumentException(
        "Either -key or -pbe is required for encryption or decryption.");
    }
  }


  /**
   * Generate a new encryption key to using command line arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On key generation errors.
   */
  protected void genKey(final CommandLine line)
    throws Exception
  {
    validateOptions(line);

    final SymmetricAlgorithm alg = newAlgorithm(line);
    SecretKey key;
    if (line.hasOption(OPT_PBE)) {
      key = generatePBEKey(alg, line);
    } else {
      final int size = Integer.parseInt(line.getOptionValue(OPT_GENKEY));
      System.err.println("Generating key of size " + size);
      key = SecretKeyUtils.generate(alg.getAlgorithm(), size);
    }
    CryptWriter.writeEncodedKey(key, getOutputStream(line));
    if (line.hasOption(OPT_OUTFILE)) {
      System.err.println("Wrote key to " + line.getOptionValue(OPT_OUTFILE));
    }
  }


  /**
   * Generates a PBE key from command line options including a password.
   *
   * @param  alg  Symmetric algorithm for which a compatible key should be
   * generated.
   * @param  line  Parsed command line arguments container.
   *
   * @return  Secret key from password.
   *
   * @throws  Exception  On key generation errors.
   */
  protected SecretKey generatePBEKey(
    final SymmetricAlgorithm alg,
    final CommandLine line)
    throws Exception
  {
    if (!line.hasOption(OPT_SALT)) {
      throw new IllegalArgumentException(
        "Salt is required for PBE key generation.");
    }
    if (!line.hasOption(OPT_ITERATIONS)) {
      throw new IllegalArgumentException(
        "Iteration count is required for PBE key generation.");
    }

    DigestAlgorithm digest = null;
    if (line.hasOption(OPT_DIGEST)) {
      digest = DigestAlgorithm.newInstance(line.getOptionValue(OPT_DIGEST));
    }

    String pbeScheme = null;
    if (line.hasOption(OPT_SCHEME)) {
      pbeScheme = line.getOptionValue(OPT_SCHEME).toLowerCase();
    }

    final char[] pass = line.getOptionValue(OPT_PBE).toCharArray();
    final byte[] salt = hexConv.toBytes(line.getOptionValue(OPT_SALT));
    final int iterations = Integer.parseInt(
      line.getOptionValue(OPT_ITERATIONS));
    final int keySize = line.hasOption(OPT_KEYSIZE)
      ? Integer.parseInt(line.getOptionValue(OPT_KEYSIZE)) : -1;
    final KeyGenerator generator;
    final byte[] derivedKey;
    final byte[] derivedIV;
    if ("pkcs12".equals(pbeScheme)) {
      if (digest == null) {
        throw new IllegalArgumentException("pkcs12 requires a digest.");
      }
      if (keySize < 0) {
        throw new IllegalArgumentException(
          "Key size is required for pkcs5s2 PBE key generation.");
      }
      System.err.println("Generating PKCS#12 PBE key.");
      generator = new PKCS12KeyGenerator(digest, salt, iterations);
      derivedKey = generator.generate(pass, keySize);
      derivedIV = generator.generate(pass, alg.getBlockSize() * 8);
    } else if ("pkcs5s1".equals(pbeScheme)) {
      if (digest == null) {
        throw new IllegalArgumentException("pkcs5s1 requires a digest.");
      }
      System.err.println("Generating PKCS#5 PBE key using PBKDF1 scheme.");
      generator = new PBKDF1KeyGenerator(digest, salt, iterations);

      final byte[] keyWithIV = generator.generate(pass, 128);
      derivedKey = new byte[8];
      derivedIV = new byte[8];
      System.arraycopy(keyWithIV, 0, derivedKey, 0, 8);
      System.arraycopy(keyWithIV, 8, derivedIV, 0, 16);
    } else if ("openssl".equals(pbeScheme)) {
      if (keySize < 0) {
        throw new IllegalArgumentException(
          "Key size is required for pkcs5s2 PBE key generation.");
      }
      System.err.println("Generating OpenSSL PBE key.");
      generator = new OpenSSLKeyGenerator(salt);
      derivedKey = generator.generate(pass, keySize);
      derivedIV = generator.generate(pass, alg.getBlockSize() * 8);
    } else {
      // Default is pkcs5s2
      if (digest != null) {
        System.err.println("Ignoring digest for pkcs5s2 PBE scheme.");
      }
      if (keySize < 0) {
        throw new IllegalArgumentException(
          "Key size is required for pkcs5s2 PBE key generation.");
      }
      System.err.println("Generating PKCS#5 PBE key using PBKDF2 scheme.");
      generator = new PBKDF2KeyGenerator(salt, iterations);
      derivedKey = generator.generate(pass, keySize);
      derivedIV = generator.generate(pass, alg.getBlockSize() * 8);
    }
    System.err.println("Derived key: " + hexConv.fromBytes(derivedKey));
    System.err.println("Derived iv: " + hexConv.fromBytes(derivedIV));
    return new SecretKeySpec(derivedKey, alg.getAlgorithm());
  }


  /**
   * Creates a symmetric key from a file defined by CLI arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  Symmetric encryption/decryption key.
   *
   * @throws  CryptException  On cryptographic errors.
   * @throws  IOException  On IO errors.
   */
  protected SecretKey readKey(final CommandLine line)
    throws CryptException, IOException
  {
    return
      CryptReader.readSecretKey(
        new File(line.getOptionValue(OPT_KEY)),
        line.getOptionValue(OPT_CIPHER));
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
  }


  /**
   * Gets a password-based encryption scheme based on command line arguments.
   *
   * @param  alg  Symmetric cipher algorithm.
   * @param  line  parsed command line arguments container.
   *
   * @return  Initialized encryption scheme.
   */
  protected EncryptionScheme getPBEScheme(
    final SymmetricAlgorithm alg,
    final CommandLine line)
  {
    if (!line.hasOption(OPT_SALT)) {
      throw new IllegalArgumentException(
        "Salt is required for PBE encryption/decryption.");
    }
    if (!line.hasOption(OPT_ITERATIONS)) {
      throw new IllegalArgumentException(
        "Iteration count is required for PBE encryption/decryption.");
    }

    DigestAlgorithm digest = null;
    if (line.hasOption(OPT_DIGEST)) {
      digest = DigestAlgorithm.newInstance(line.getOptionValue(OPT_DIGEST));
    }

    String scheme = null;
    if (line.hasOption(OPT_SCHEME)) {
      scheme = line.getOptionValue(OPT_SCHEME).toLowerCase();
    }

    final byte[] salt = hexConv.toBytes(line.getOptionValue(OPT_SALT));
    final int iterations = Integer.parseInt(
      line.getOptionValue(OPT_ITERATIONS));
    final int keySize = line.hasOption(OPT_KEYSIZE)
      ? Integer.parseInt(line.getOptionValue(OPT_KEYSIZE)) : 0;
    final EncryptionScheme pbeScheme;
    if ("pkcs12".equals(scheme)) {
      if (digest == null) {
        throw new IllegalArgumentException("pkcs12 requires a digest.");
      }
      if (keySize < 0) {
        throw new IllegalArgumentException(
          "Key size is required for pkcs5s2 PBE key generation.");
      }
      System.err.println("Using PKCS#12 PBE encryption scheme.");
      pbeScheme = new PKCS12EncryptionScheme(
        alg,
        digest,
        new PBEParameter(salt, iterations),
        keySize);
    } else if ("pkcs5s1".equals(scheme)) {
      if (digest == null) {
        throw new IllegalArgumentException("pkcs12 requires a digest.");
      }
      System.err.println("Using PKCS#5 PBES1 encryption scheme.");
      pbeScheme = new PBES1EncryptionScheme(
        alg,
        digest,
        new PBEParameter(salt, iterations));
    } else if ("openssl".equals(scheme)) {
      if (keySize < 0) {
        throw new IllegalArgumentException(
          "Key size is required for pkcs5s2 PBE key generation.");
      }
      System.err.println("Using OpenSSL encryption scheme.");
      pbeScheme = new OpenSSLEncryptionScheme(alg, salt, keySize);
    } else {
      // Default is pkcs5s2
      if (digest != null) {
        System.err.println("Ignoring digest for pkcs5s2 PBE scheme.");
      }
      if (keySize < 0) {
        throw new IllegalArgumentException(
          "Key size is required for pkcs5s2 PBE key generation.");
      }
      System.err.println("Using PKCS#5 PBES2 encryption scheme.");
      pbeScheme = new PBES2EncryptionScheme(
        alg,
        new PBKDF2Parameters(salt, iterations, keySize / 8));
    }
    if (line.hasOption(OPT_IV)) {
      System.err.println("Using provided IV instead of generated value.");
      alg.setIV(hexConv.toBytes(line.getOptionValue(OPT_IV)));
    }
    return pbeScheme;
  }
}
