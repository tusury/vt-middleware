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
package edu.vt.middleware.crypt.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.CryptProvider;

/**
 * Base class for credential readers that handle types related to X.509
 * cryptographic types.
 *
 * @param  <T>  Cryptographic type read by this class.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractX509CredentialReader<T>
  implements CredentialReader<T>
{

  /** Certificate type. */
  private static final String CERTIFICATE_TYPE = "X.509";

  /** X.509 certificate factory. */
  private CertificateFactory factory;


  /** {@inheritDoc} */
  public T read(final File file)
    throws IOException, CryptException
  {
    return read(new BufferedInputStream(new FileInputStream(file)));
  }


  /**
   * Gets a certificate factory for handling X.509 certificates and related
   * objects.
   *
   * @return  X.509 certificate factory.
   *
   * @throws  CryptException  On provider errors creating certificate factory of
   * X.509 type.
   */
  protected CertificateFactory getX509CertificateFactory()
    throws CryptException
  {
    if (factory == null) {
      factory = CryptProvider.getCertificateFactory(CERTIFICATE_TYPE);
    }
    return factory;
  }
}
