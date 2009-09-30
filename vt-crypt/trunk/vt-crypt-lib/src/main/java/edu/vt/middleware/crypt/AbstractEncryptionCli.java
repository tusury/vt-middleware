/*
  $Id: AbstractEncryptionCli.java 12 2008-11-17 19:13:16Z marvin.addison $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 12 $
  Updated: $Date: 2008-11-17 14:13:16 -0500 (Mon, 17 Nov 2008) $
*/
package edu.vt.middleware.crypt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import edu.vt.middleware.crypt.io.Base64FilterInputStream;
import edu.vt.middleware.crypt.io.Base64FilterOutputStream;
import edu.vt.middleware.crypt.io.HexFilterInputStream;
import edu.vt.middleware.crypt.io.HexFilterOutputStream;
import edu.vt.middleware.crypt.io.TeePrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Abstract base class for CLI handler of encryption operations.
 *
 * @author  Middleware Services
 * @version  $Revision: 12 $
 */
public abstract class AbstractEncryptionCli extends AbstractCli
{

  /** Cipher option. */
  protected static final String OPT_CIPHER = "cipher";

  /** Output file option. */
  protected static final String OPT_OUTFILE = "out";

  /** Cihpertext encoding format. */
  protected static final String OPT_ENCODING = "encoding";

  /** Encrypt operation option. */
  protected static final String OPT_ENCRYPT = "encrypt";

  /** Decrypt operation option. */
  protected static final String OPT_DECRYPT = "decrypt";

  /** Tail output option. */
  protected static final String OPT_TAIL = "tail";


  /** {@inheritDoc} */
  protected void initOptions()
  {
    super.initOptions();

    final Option cipher = new Option(OPT_CIPHER, true, "cipher algorithm");
    cipher.setArgName("algname");
    cipher.setOptionalArg(false);

    final Option infile = new Option(
      OPT_INFILE,
      true,
      "file to encrypt/decrypt; defaults to STDIN");
    infile.setArgName("filepath");
    infile.setOptionalArg(false);

    final Option outfile = new Option(
      OPT_OUTFILE,
      true,
      "output file containing result; defaults to STDOUT");
    outfile.setArgName("filepath");
    outfile.setOptionalArg(false);

    final Option encoding = new Option(
      OPT_ENCODING,
      true,
      "ciphertext encoding format, either base64 or hex");
    encoding.setArgName("format");
    encoding.setOptionalArg(false);

    options.addOption(cipher);
    options.addOption(infile);
    options.addOption(outfile);
    options.addOption(encoding);
    options.addOption(new Option(OPT_TAIL, "tail output from operation"));
  }


  /**
   * Encrypt the given plaintext input stream into the output stream using the
   * given encryption cipher algorithm.
   *
   * @param  alg  Encryption cipher algorithm.
   * @param  in  Input stream containing plaintext.
   * @param  out  Output stream containing ciphertext.
   *
   * @throws  Exception  On encryption errors.
   */
  protected void encrypt(
    final EncryptionAlgorithm alg,
    final InputStream in,
    final OutputStream out)
    throws Exception
  {
    try {
      System.err.println("Beginning encryption.");
      alg.initEncrypt();
      alg.encrypt(in, out);
    } catch (Exception ex) {
      throw ex;
    } finally {
      closeStream(in);
      closeStream(out);
    }
    System.err.println("Encryption operation complete.");
  }


  /**
   * Decrypt the given ciphertext input stream into the output stream using the
   * given encryption cipher algorithm.
   *
   * @param  alg  Encryption cipher algorithm.
   * @param  in  Input stream containing ciphertext.
   * @param  out  Output stream containing plaintext.
   *
   * @throws  Exception  On encryption errors.
   */
  protected void decrypt(
    final EncryptionAlgorithm alg,
    final InputStream in,
    final OutputStream out)
    throws Exception
  {
    try {
      System.err.println("Beginning decryption.");
      alg.initDecrypt();
      alg.decrypt(in, out);
    } catch (Exception ex) {
      throw ex;
    } finally {
      closeStream(in);
      closeStream(out);
    }
    System.err.println("Decryption operation complete.");
  }


  /**
   * Get an input stream containing data to be encrypted or decrypted based on
   * CLI arguments.
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
    InputStream in = super.getInputStream(line);
    if (line.hasOption(OPT_DECRYPT) && line.hasOption(OPT_ENCODING)) {
      final String encName = line.getOptionValue(OPT_ENCODING);
      if (BASE_64_ENCODING.equals(encName)) {
        in = new Base64FilterInputStream(in);
      } else if (HEX_ENCODING.equals(encName)) {
        in = new HexFilterInputStream(in);
      } else {
        throw new IllegalArgumentException("Unknown encoding.");
      }
    }
    return in;
  }


  /**
   * Get an output stream containing data to be encrypted or decrypted based on
   * CLI arguments.
   *
   * @param  line  Parsed command line arguments container.
   *
   * @return  Output stream.
   *
   * @throws  IOException  On stream creation errors.
   */
  protected OutputStream getOutputStream(final CommandLine line)
    throws IOException
  {
    OutputStream out = null;
    if (line.hasOption(OPT_OUTFILE)) {
      final File file = new File(line.getOptionValue(OPT_OUTFILE));
      System.err.println("Writing output to " + file);
      if (line.hasOption(OPT_TAIL)) {
        out = new TeePrintStream(
          new BufferedOutputStream(new FileOutputStream(file)),
          System.out);
      } else {
        out = new BufferedOutputStream(new FileOutputStream(file));
      }
    } else {
      System.err.println("Writing output to STDOUT");
      out = System.out;
    }
    if (line.hasOption(OPT_ENCRYPT) && line.hasOption(OPT_ENCODING)) {
      final String encName = line.getOptionValue(OPT_ENCODING);
      if (BASE_64_ENCODING.equals(encName)) {
        out = new Base64FilterOutputStream(out);
      } else if (HEX_ENCODING.equals(encName)) {
        out = new HexFilterOutputStream(out);
      } else {
        throw new IllegalArgumentException("Unknown encoding.");
      }
    }
    return out;
  }

}
