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
package edu.vt.middleware.crypt.symmetric;

import java.io.File;
import java.io.IOException;
import javax.crypto.SecretKey;
import edu.vt.middleware.crypt.AbstractEncryptionCli;
import edu.vt.middleware.crypt.KeyWithIV;
import edu.vt.middleware.crypt.PbeKeyGenerator;
import edu.vt.middleware.crypt.digest.DigestAlgorithm;
import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.util.CryptWriter;
import edu.vt.middleware.crypt.util.HexConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

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
  protected static final String OPT_PBEMODE = "pbemode";

  /** Digest algorithm used with PBE modes that allow it. */
  protected static final String OPT_DIGEST = "digest";

  /** Salt for PBE key generation. */
  protected static final String OPT_SALT = "salt";

  /** Generate key option. */
  protected static final String OPT_GENKEY = "genkey";

  /** Name of operation provided by this class. */
  private static final String COMMAND_NAME = "enc";

  /** 8 bits in one byte. */
  private static final int BITS_IN_BYTE = 8;


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
    final SymmetricCli cli = new SymmetricCli();
    cli.initOptions();
    try {
      if (args.length > 0) {
        final CommandLine line = parser.parse(cli.options, args);
        if (line.hasOption(OPT_ENCRYPT)) {
          cli.encrypt(line);
        } else if (line.hasOption(OPT_DECRYPT)) {
          cli.decrypt(line);
        } else if (line.hasOption(OPT_GENKEY)) {
          cli.genKey(line);
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

    final Option mode = new Option(OPT_MODE, true, "cipher mode, e.g. CBC");
    mode.setArgName("name");
    mode.setOptionalArg(false);

    final Option padding = new Option(
      OPT_PADDING,
      true,
      "cipher padding strategy, e.g. PKCS5Padding");
    padding.setArgName("name");
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
      "generate PBE key from passphrase; uses pkcs5s2 by default");
    pbe.setArgName("passphrase");
    pbe.setOptionalArg(false);

    final Option pbeMode = new Option(
      OPT_PBEMODE,
      true,
      "PBE key generation mode; one of pkcs5s1, pkcs5s2, openssl, pkcs12");
    pbeMode.setArgName("name");
    pbeMode.setOptionalArg(false);

    final Option pbeDigest = new Option(
      OPT_DIGEST,
      true,
      "digest algorithm to use with PBE mode pkcs5s1 or pkcs12");
    pbeDigest.setArgName("algname");
    pbeDigest.setOptionalArg(false);

    final Option salt = new Option(
      OPT_SALT,
      true,
      "salt for PBE key generation in hex");
    salt.setArgName("hex_salt");
    salt.setOptionalArg(false);

    options.addOption(mode);
    options.addOption(padding);
    options.addOption(key);
    options.addOption(keySize);
    options.addOption(iv);
    options.addOption(pbe);
    options.addOption(pbeMode);
    options.addOption(pbeDigest);
    options.addOption(salt);
    options.addOption(new Option(OPT_GENKEY, "generate new encryption key"));
    options.addOption(new Option(OPT_ENCRYPT, "perform encryption"));
    options.addOption(new Option(OPT_DECRYPT, "perform decryption"));
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
    SymmetricAlgorithm algorithm = null;
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


  /**
   * Initialize the given symmetric algorithm in preparation for an encryption
   * or decryption operation.
   *
   * @param  alg  Algorith to initialize.
   * @param  line  Parsed command line arguments container.
   *
   * @throws  Exception  On errors.
   */
  protected void initAlgorithm(
    final SymmetricAlgorithm alg,
    final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_KEY)) {
      alg.setKey(readKey(line));
      if (line.hasOption(OPT_IV)) {
        alg.setIV(hexConv.toBytes(line.getOptionValue(OPT_IV)));
      }
    } else if (line.hasOption(OPT_PBE)) {
      final KeyWithIV keyWithIV = genPbeKeyWithIV(alg, line);
      alg.setKey(keyWithIV.getKey());
      if (line.hasOption(OPT_IV)) {
        alg.setIV(hexConv.toBytes(line.getOptionValue(OPT_IV)));
      } else if (keyWithIV.getIV().length > 0) {
        alg.setIV(keyWithIV.getIV());
      }
    } else {
      throw new IllegalArgumentException(
        "Either -key or -pbe is required for encryption or decryption.");
    }
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
    final SymmetricAlgorithm alg = newAlgorithm(line);
    initAlgorithm(alg, line);
    encrypt(alg, getInputStream(line), getOutputStream(line));
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
    final SymmetricAlgorithm alg = newAlgorithm(line);
    initAlgorithm(alg, line);
    decrypt(alg, getInputStream(line), getOutputStream(line));
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
    final SymmetricAlgorithm alg = newAlgorithm(line);
    SecretKey key = null;
    if (line.hasOption(OPT_PBE)) {
      key = genPbeKeyWithIV(alg, line).getKey();
    } else {
      if (line.hasOption(OPT_KEYSIZE)) {
        final int size = Integer.parseInt(line.getOptionValue(OPT_KEYSIZE));
        System.err.println("Generating key of size " + size);
        key = alg.generateKey(size);
      } else {
        System.err.println("Generating key of default size for " + alg);
        key = alg.generateKey();
      }
    }
    CryptWriter.writeEncodedKey(key, getOutputStream(line));
    if (line.hasOption(OPT_OUTFILE)) {
      System.err.println("Wrote key to " + line.getOptionValue(OPT_OUTFILE));
    }
  }


  /**
   * Generates a PBE key/IV pair from command line options.
   *
   * @param  alg  Symmetric algorithm for which a compatible key should be
   * generated.
   * @param  line  Parsed command line arguments container.
   *
   * @return  Secret key from password.
   *
   * @throws  Exception  On key generation errors.
   */
  protected KeyWithIV genPbeKeyWithIV(
    final SymmetricAlgorithm alg,
    final CommandLine line)
    throws Exception
  {
    if (!line.hasOption(OPT_SALT)) {
      throw new IllegalArgumentException(
        "Salt is required for PBE key generation.");
    }
    if (!line.hasOption(OPT_KEYSIZE)) {
      throw new IllegalArgumentException(
        "Key size is required for PBE key generation.");
    }

    KeyWithIV keyWithIV = null;
    DigestAlgorithm digest = null;
    if (line.hasOption(OPT_DIGEST)) {
      digest = DigestAlgorithm.newInstance(line.getOptionValue(OPT_DIGEST));
    }

    String pbeMode = null;
    if (line.hasOption(OPT_PBEMODE)) {
      pbeMode = line.getOptionValue(OPT_PBEMODE).toLowerCase();
    }

    final int keySize = Integer.parseInt(line.getOptionValue(OPT_KEYSIZE));
    int ivSize = 0;
    if (!line.hasOption(OPT_IV)) {
      // Generate an IV from the password if none specified
      ivSize = alg.getBlockSize() * BITS_IN_BYTE;
    }

    final PbeKeyGenerator keyGen = new PbeKeyGenerator(alg);
    final char[] pass = line.getOptionValue(OPT_PBE).toCharArray();
    final byte[] salt = hexConv.toBytes(line.getOptionValue(OPT_SALT));
    if ("pkcs12".equals(pbeMode)) {
      if (digest == null) {
        throw new IllegalArgumentException(
          "pkcs12 requires a digest algorithm");
      }
      System.err.println("Generating PKCS#12 PBE key.");
      keyWithIV = keyGen.generatePkcs12(pass, keySize, ivSize, digest, salt);
    } else if ("pkcs5s1".equals(pbeMode)) {
      if (digest == null) {
        throw new IllegalArgumentException(
          "pkcs5s1 requires a digest algorithm");
      }
      System.err.println("Generating PKCS#5 v1 PBE key.");
      keyWithIV = keyGen.generatePkcs5v1(pass, keySize, ivSize, digest, salt);
    } else if ("openssl".equals(pbeMode)) {
      System.err.println("Generating OpenSSL PBE key.");
      keyWithIV = keyGen.generateOpenssl(pass, keySize, ivSize, salt);
    } else {
      // Default is pkcs5s2
      System.err.println("Generating PKCS#5 v2 PBE key.");
      keyWithIV = keyGen.generatePkcs5v2(pass, keySize, ivSize, salt);
    }
    System.err.println(
      "Key: " + hexConv.fromBytes(keyWithIV.getKey().getEncoded()));
    if (keyWithIV.getIV().length > 0) {
      System.err.println("IV: " + hexConv.fromBytes(keyWithIV.getIV()));
    }
    return keyWithIV;
  }


  /**
   * Creates a symmetric key from a file defined by CLI arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  Symmetric encryption/decryption key.
   *
   * @throws  IOException  On IO errors.
   */
  protected SecretKey readKey(final CommandLine line)
    throws IOException
  {
    return
      CryptReader.readSecretKey(
        new File(line.getOptionValue(OPT_KEY)),
        line.getOptionValue(OPT_CIPHER));
  }
}
