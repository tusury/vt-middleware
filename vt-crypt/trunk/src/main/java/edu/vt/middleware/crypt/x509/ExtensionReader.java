/*
  $Id$

  Copyright (C) 2007-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.crypt.CryptException;
import edu.vt.middleware.crypt.util.DERHelper;
import edu.vt.middleware.crypt.x509.types.AccessDescriptionList;
import edu.vt.middleware.crypt.x509.types.AuthorityKeyIdentifier;
import edu.vt.middleware.crypt.x509.types.BasicConstraints;
import edu.vt.middleware.crypt.x509.types.DistributionPointList;
import edu.vt.middleware.crypt.x509.types.GeneralNameList;
import edu.vt.middleware.crypt.x509.types.KeyIdentifier;
import edu.vt.middleware.crypt.x509.types.KeyPurposeIdList;
import edu.vt.middleware.crypt.x509.types.KeyUsage;
import edu.vt.middleware.crypt.x509.types.PolicyInformationList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.DEREncodable;

/**
 * Reads X.509v3 extended properties from an {@link X509Certificate} object. The
 * available properties are described in section 4.2 of RFC 2459,
 * http://www.faqs.org/rfcs/rfc2459.html.
 *
 * @author  Middleware Services
 * @version  $Revision: $
 */
public final class ExtensionReader
{

  /** Logger instance. */
  private final Log logger = LogFactory.getLog(getClass());

  /** The X509Certificate whose extension fields will be read. */
  private X509Certificate certificate;


  /**
   * Creates a new instance that can read extension fields from the given X.509
   * certificate.
   *
   * @param  cert  Certificate to read.
   */
  public ExtensionReader(final X509Certificate cert)
  {
    certificate = cert;
  }


  /**
   * Reads the value of the extension given by OID or name as defined in section
   * 4.2 of RFC 2459.
   *
   * @param  extensionOidOrName  OID or extension name, e.g. 2.5.29.14 or
   * SubjectKeyIdentifier. In the case of extension name, the name is
   * case-sensitive and follows the conventions in RFC 2459.
   *
   * @return  Extension type containing data from requested extension field.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   * @throws  IllegalArgumentException  On invalid OID or extension name.
   */
  public Object read(final String extensionOidOrName)
    throws CryptException
  {
    if (extensionOidOrName == null) {
      throw new IllegalArgumentException("extensionOidOrName cannot be null.");
    }
    if (extensionOidOrName.contains(".")) {
      return read(ExtensionType.fromOid(extensionOidOrName));
    } else {
      return read(ExtensionType.fromName(extensionOidOrName));
    }
  }


  /**
   * Reads the value of the given certificate extension field.
   *
   * @param  extension  Extension to read from certificate.
   *
   * @return  An extension type from the <code>
   * edu.vt.middleware.crypt.x509.types</code> package containing the data in
   * the extension field.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public Object read(final ExtensionType extension)
    throws CryptException
  {
    final DEREncodable value = readObject(extension);
    if (value != null) {
      return ExtensionFactory.createInstance(readObject(extension), extension);
    } else {
      return null;
    }
  }


  /**
   * Reads the value of the SubjectAlternativeName extension field of the
   * certificate.
   *
   * @return  Collection of subject alternative names or null if the certificate
   * does not define this extension field. Note that an empty collection of
   * names is different from a null return value; in the former case the field
   * is defined but empty, whereas in the latter the field is not defined on the
   * certificate.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public GeneralNameList readSubjectAlternativeName()
    throws CryptException
  {
    return
      ExtensionFactory.createGeneralNameList(
        readObject(ExtensionType.SubjectAlternativeName));
  }


  /**
   * Reads the value of the <code>IssuerAlternativeName</code> extension field
   * of the certificate.
   *
   * @return  Collection of issuer alternative names or null if the certificate
   * does not define this extension field. Note that an empty collection of
   * names is different from a null return value; in the former case the field
   * is defined but empty, whereas in the latter the field is not defined on the
   * certificate.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public GeneralNameList readIssuerAlternativeName()
    throws CryptException
  {
    return
      ExtensionFactory.createGeneralNameList(
        readObject(ExtensionType.IssuerAlternativeName));
  }


  /**
   * Reads the value of the <code>BasicConstraints</code> extension field of the
   * certificate.
   *
   * @return  Basic constraints defined on certificate or null if the
   * certificate does not define the field.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public BasicConstraints readBasicConstraints()
    throws CryptException
  {
    return
      ExtensionFactory.createBasicConstraints(
        readObject(ExtensionType.BasicConstraints));
  }


  /**
   * Reads the value of the <code>CertificatePolicies</code> extension field of
   * the certificate.
   *
   * @return  List of certificate policies defined on certificate or null if the
   * certificate does not define the field.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public PolicyInformationList readCertificatePolicies()
    throws CryptException
  {
    return
      ExtensionFactory.createPolicyInformationList(
        readObject(ExtensionType.CertificatePolicies));
  }


  /**
   * Reads the value of the <code>SubjectKeyIdentifier</code> extension field of
   * the certificate.
   *
   * @return  Subject key identifier.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public KeyIdentifier readSubjectKeyIdentifier()
    throws CryptException
  {
    return
      ExtensionFactory.createKeyIdentifier(
        readObject(ExtensionType.SubjectKeyIdentifier));
  }


  /**
   * Reads the value of the <code>AuthorityKeyIdentifier</code> extension field
   * of the certificate.
   *
   * @return  Authority key identifier.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public AuthorityKeyIdentifier readAuthorityKeyIdentifier()
    throws CryptException
  {
    return
      ExtensionFactory.createAuthorityKeyIdentifier(
        readObject(ExtensionType.AuthorityKeyIdentifier));
  }


  /**
   * Reads the value of the <code>KeyUsage</code> extension field of the
   * certificate.
   *
   * @return  Key usage data.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public KeyUsage readKeyUsage()
    throws CryptException
  {
    return ExtensionFactory.createKeyUsage(readObject(ExtensionType.KeyUsage));
  }


  /**
   * Reads the value of the <code>ExtendedKeyUsage</code> extension field of the
   * certificate.
   *
   * @return  List of supported extended key usages.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public KeyPurposeIdList readExtendedKeyUsage()
    throws CryptException
  {
    return
      ExtensionFactory.createKeyPurposeIdList(
        readObject(ExtensionType.ExtendedKeyUsage));
  }


  /**
   * Reads the value of the <code>CRLDistributionPoints</code> extension field
   * of the certificate.
   *
   * @return  List of CRL distribution points.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public DistributionPointList readCRLDistributionPoints()
    throws CryptException
  {
    return
      ExtensionFactory.createDistributionPointList(
        readObject(ExtensionType.CRLDistributionPoints));
  }


  /**
   * Reads the value of the <code>AuthorityInformationAccess</code> extension
   * field of the certificate.
   *
   * @return  List of CRL distribution points.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public AccessDescriptionList readAuthorityInformationAccess()
    throws CryptException
  {
    return
      ExtensionFactory.createAccessDescriptionList(
        readObject(ExtensionType.AuthorityInformationAccess));
  }


  /**
   * Attempts to read all extensions defined in section 4.2 of RFC 2459 and
   * returns a map of all extensions defined on the certificate.
   *
   * @return  Map of extension types to extension data.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  public Map<ExtensionType, Object> readAll()
    throws CryptException
  {
    final Map<ExtensionType, Object> extMap =
      new HashMap<ExtensionType, Object>(ExtensionType.values().length);
    for (ExtensionType type : ExtensionType.values()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Reading " + type);
      }

      final Object extension = read(type);
      if (extension != null) {
        extMap.put(type, extension);
      }
    }
    return extMap;
  }


  /**
   * Reads the extension field of the given type from the certificate as an
   * ASN.1 encodable object.
   *
   * @param  type  Extension type.
   *
   * @return  DER encoded object containing data for the given extension type or
   * null if there is no such extension defined on the certificate.
   *
   * @throws  CryptException  On errors reading encoded certificate extension
   * field data.
   */
  private DEREncodable readObject(final ExtensionType type)
    throws CryptException
  {
    final byte[] data = certificate.getExtensionValue(type.getOid());
    if (data == null) {
      return null;
    }
    try {
      return DERHelper.toDERObject(data, true);
    } catch (IOException e) {
      throw new CryptException(
        "Error reading certificate extension " + type,
        e);
    }
  }
}
