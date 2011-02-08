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
package edu.vt.middleware.crypt.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.util.PemHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.DERObjectIdentifier;

/**
 * Base class for credential readers that handle credentials that can be
 * described as a byte array.
 *
 * @param  <T>  Type of credential handled by this class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractEncodedCredentialReader<T>
  implements CredentialReader<T>
{

  /** DSA algorithm OID. */
  protected static final DERObjectIdentifier DSA_ID = new DERObjectIdentifier(
    "1.2.840.10040.4.1");

  /** RSA algorithm OID. */
  protected static final DERObjectIdentifier RSA_ID = new DERObjectIdentifier(
    "1.2.840.113549.1.1.1");

  /** Logger instance. */
  protected final Log logger = LogFactory.getLog(getClass());


  /** {@inheritDoc} */
  public T read(final File file)
    throws IOException, CryptException
  {
    byte[] data = IOHelper.read(new FileInputStream(file).getChannel());
    if (PemHelper.isPem(data)) {
      data = PemHelper.decode(data);
    }
    return decode(data);
  }


  /** {@inheritDoc} */
  public T read(final InputStream in)
    throws IOException, CryptException
  {
    byte[] data = IOHelper.read(in);
    if (PemHelper.isPem(data)) {
      data = PemHelper.decode(data);
    }
    return decode(data);
  }


  /**
   * Decodes an encoded representation of a credential into a corresponding
   * object.
   *
   * @param  encoded  Encoded representation of credential.
   *
   * @return  Decoded credential.
   *
   * @throws  CryptException  On decoding errors.
   */
  protected abstract T decode(final byte[] encoded)
    throws CryptException;
}
