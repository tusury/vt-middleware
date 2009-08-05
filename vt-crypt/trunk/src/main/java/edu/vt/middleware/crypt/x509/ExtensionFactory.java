/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509;

import java.util.ArrayList;
import java.util.List;

import edu.vt.middleware.crypt.x509.types.GeneralName;
import edu.vt.middleware.crypt.x509.types.GeneralNameType;
import edu.vt.middleware.crypt.x509.types.GeneralNames;

import org.bouncycastle.asn1.ASN1Encodable;

/**
 * Creates X.509v3 extension types in the VT Crypt namespace from
 * BouncyCastle ASN1Encodable types.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public final class ExtensionFactory
{
  /** Private default constructor for utility class */
  private ExtensionFactory() {}


  /**
   * Creates an instance of a VT Crypt X.509v3 extension type from the
   * corresponding Bouncy Castle extension type.
   *
   * @param  type  Type of extension.
   * @param  bcExtension  Bouncy Castle ASN.1 type of X.509v3 extension.
   *
   * @return  An extension type from the
   * <code>edu.vt.middleware.crypt.x509.types</code> package that is
   * semantically equivalent to the given Bouncy Castle object.
   *
   * @throws  IllegalArgumentException  If given ASN.1 encodable object is
   * not compatible with the given extension type.
   */
  public static Object createInstance(
      final ASN1Encodable bcExtension,
      final ExtensionType type)
  {
    Object extension = null;
    try {
      switch (type) {
      case AuthorityInfoAccess:
        break;
      case AuthorityKeyIdentifier:
        break;
      case BasicConstraints:
        break;
      case CertificatePolicies:
        break;
      case CRLDistributionPoints:
        break;
      case ExtendedKeyUsage:
        break;
      case IssuerAlternativeName:
      case SubjectAlternativeName:
        extension = createGeneralNames(bcExtension);
        break;
      case KeyUsage:
        break;
      case PolicyConstraints:
        break;
      case PolicyMappings:
        break;
      case PrivateKeyUsagePeriod:
        break;
      case SubjectDirectoryAttributes:
        break;
      case SubjectKeyIdentifier:
        break;
      default:
        break;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("%s is not compatible with %s.", bcExtension, type, e));
    }
    return extension;
  }


  /**
   * Creates a {@link GeneralNames} object from the corresponding Bouncy Castle
   * type.
   *
   * @param  names  ASN.1 encoded general names data.
   *
   * @return  Object that is semantically equivalent to given BC type.
   */
  public static GeneralNames createGeneralNames(final ASN1Encodable names)
  {
    final List<GeneralName> nameList = new ArrayList<GeneralName>();
    for (org.bouncycastle.asn1.x509.GeneralName name :
      org.bouncycastle.asn1.x509.GeneralNames.getInstance(names).getNames())
    {
      nameList.add(new GeneralName(
          name.getName().toString(),
          GeneralNameType.fromTagNumber(name.getTagNo())));
    }
    return new GeneralNames(nameList);
  }
}
