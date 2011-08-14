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
package edu.vt.middleware.crypt.tasks;

import java.io.File;
import java.io.IOException;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.symmetric.SymmetricAlgorithm;
import edu.vt.middleware.crypt.util.CryptReader;
import edu.vt.middleware.crypt.util.HexConverter;
import org.apache.tools.ant.Task;

/**
 * <p><code>AbstractCryptTask</code> provides common methods for crypt ant
 * tasks.</p>
 *
 * @author  Middleware Services
 * @version  $Revision: 21 $
 */
public abstract class AbstractCryptTask extends Task
{

  /** Property name to set. */
  protected String propertyName;

  /** Algorithm name. */
  protected String algorithm = "AES";

  /** Mode used for encryption and decryption. */
  protected String mode = "CBC";

  /** Padding used for encryption and decryption. */
  protected String padding = "PKCS5Padding";

  /** Initialization vector for encryption/decryption. */
  protected String iv;

  /** Private key to read. */
  protected File privateKey;


  /**
   * <p>This sets the name of the property to set with the decrypted value.</p>
   *
   * @param  s  <code>String</code>
   */
  public void setName(final String s)
  {
    this.propertyName = s;
  }


  /**
   * <p>This sets the algorithm used for decryption.</p>
   *
   * @param  s  <code>String</code>
   */
  public void setAlgorithm(final String s)
  {
    this.algorithm = s;
  }


  /**
   * <p>This sets the mode used for decryption.</p>
   *
   * @param  s  <code>String</code>
   */
  public void setMode(final String s)
  {
    this.mode = s;
  }


  /**
   * <p>This sets the padding used for decryption.</p>
   *
   * @param  s  <code>String</code>
   */
  public void setPadding(final String s)
  {
    this.padding = s;
  }


  /**
   * <p>This sets the initialization vector used for encryption/decryption.</p>
   *
   * @param  s  <code>String</code>
   */
  public void setIv(final String s)
  {
    this.iv = s;
  }


  /**
   * <p>This sets the private key used for decryption.</p>
   *
   * @param  f  <code>File</code>
   */
  public void setPrivateKey(final File f)
  {
    this.privateKey = f;
  }


  /** <p>See @link{org.apache.tools.ant.Task}.</p> */
  public abstract void execute();


  /**
   * Creates a new symmetric algorithm cipher and initializes it using task
   * properties.
   *
   * @return  New instance ready for use.
   *
   * @throws  CryptException  On cryptographic errors.
   * @throws  IOException  On IO errors reading symmetric key.
   */
  protected SymmetricAlgorithm createAlgorithm()
    throws CryptException, IOException
  {
    final SymmetricAlgorithm crypt = SymmetricAlgorithm.newInstance(
      this.algorithm,
      this.mode,
      this.padding);
    if (this.iv != null) {
      crypt.setIV(new HexConverter().toBytes(this.iv));
    }
    crypt.setKey(CryptReader.readSecretKey(this.privateKey, this.algorithm));
    return crypt;
  }
}
