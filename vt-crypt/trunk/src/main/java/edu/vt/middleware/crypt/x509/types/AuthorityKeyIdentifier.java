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
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of the <code>AuthorityKeyIdentifier</code> type defined in
 * section 4.2.1.1 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class AuthorityKeyIdentifier
{
  /** Hash code scale factor */
  private static final int HASH_FACTOR = 31;

  /** Identifies the key used by issuer to sign this cert */
  private KeyIdentifier keyIdentifier;

  /** Isser names */
  private GeneralNameList authorityCertIssuer;

  /** Certificate serial number of issuer cert */
  private Integer authorityCertSerialNumber;


  /**
   * Creates an empty authority key identifier.  Although this is technically
   * supported by RFC 2459, an empty authority key identifier is meaningless.
   * We support it here to be strictly conformant with the RFC.
   */
  public AuthorityKeyIdentifier() {}


  /**
   * Creates a new instance with the given key identifier.
   *
   * @param  id  Issuer public key identifier.
   */
  public AuthorityKeyIdentifier(final KeyIdentifier id)
  {
    this(id, null, null);
  }


  /**
   * Creates a new instance with all fields defined.
   *
   * @param  id  Issuer public key identifier; may be null.
   * @param  issuerNames  General names of the certificate issuer; may be null.
   * @param  issuerSerial  Serial number of issuer certificate; may be null.
   */
  public AuthorityKeyIdentifier(
    final KeyIdentifier id,
    final GeneralNameList issuerNames,
    final Integer issuerSerial)
  {
    keyIdentifier = id;
    authorityCertIssuer = issuerNames;
    authorityCertSerialNumber = issuerSerial;
  }


  /**
   * @return  Key identifier of issuer public key matching private key used
   * to sign the certificate.
   */
  public KeyIdentifier getKeyIdentifier()
  {
    return keyIdentifier;
  }


  /**
   * @return  General names by which issuer is known.
   */
  public GeneralNameList getAuthorityCertIssuer()
  {
    return authorityCertIssuer;
  }


  /**
   * @return  Issuer certificate serial number.
   */
  public Integer getAuthorityCertSerialNumber()
  {
    return authorityCertSerialNumber;
  }


  /**
   * @return  String representation containing the ExplicitText and
   * NoticeReference fields.
   */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    int count = 0;
    if (keyIdentifier != null) {
      ++count;
      sb.append(keyIdentifier);
    }
    if (authorityCertSerialNumber != null) {
      if (++count > 1) {
        sb.append(", ");
      }
      sb.append("SN:");
      sb.append(authorityCertSerialNumber);
    }
    if (authorityCertIssuer != null) {
      if (++count > 1) {
        sb.append(", ");
      }
      sb.append(authorityCertIssuer);
    }
    return sb.toString();
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      final AuthorityKeyIdentifier other = (AuthorityKeyIdentifier) obj;
      result =
        (keyIdentifier != null
          ? keyIdentifier.equals(other.getKeyIdentifier())
          : other.getKeyIdentifier() == null) &&
        (authorityCertIssuer != null
          ? authorityCertIssuer.equals(other.getAuthorityCertIssuer())
          : other.getAuthorityCertIssuer() == null) &&
        (authorityCertSerialNumber != null
          ? authorityCertSerialNumber.equals(
              other.getAuthorityCertSerialNumber())
          : other.getAuthorityCertSerialNumber() == null);
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    if (keyIdentifier != null) {
      hash = HASH_FACTOR * hash + keyIdentifier.hashCode();
    }
    if (authorityCertSerialNumber != null) {
      hash = HASH_FACTOR * hash + authorityCertSerialNumber.hashCode();
    }
    if (authorityCertIssuer != null) {
      hash = HASH_FACTOR * hash + authorityCertIssuer.hashCode();
    }
    return hash;
  }
}
