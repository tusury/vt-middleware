package edu.vt.middleware.crypt.io;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;

import edu.vt.middleware.crypt.CryptException;

/**
 * Credential reader for handling X.509 CRLs.  Both PEM and DER encoding of CRL data is supported.
 *
 * @author Middleware Services
 * @version $Revision$ $Date$
 */
public class X509CRLCredentialReader extends AbstractX509CredentialReader<X509CRL>
{
  /** {@inheritDoc} */
  public X509CRL read(final InputStream in) throws IOException, CryptException
  {
    try {
      return (X509CRL) getX509CertificateFactory().generateCRL(in);
    } catch (CRLException e) {
      throw new CryptException("Failed reading X.509 CRL.", e);
    }
  }
}
