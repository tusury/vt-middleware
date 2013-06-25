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
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of the <code>PolicyQualifierInfo</code> type defined in
 * section 4.2.1.5 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PolicyQualifierInfo
{

  /** OID for policy qualifier containing CPS URI. */
  public static final String POLICY_QUALIFIER_ID_CPS = "1.3.6.1.5.5.7.2.1";

  /** OID for policy qualifier containing user notice. */
  public static final String POLICY_QUALIFIER_ID_UNOTICE = "1.3.6.1.5.5.7.2.2";

  /** Hash code scale factor. */
  private static final int HASH_FACTOR = 31;

  /** Policy qualifier OID. */
  private String policyQualifierId;

  /** Qualifier data -- either UserNotice or String containing CPS URI. */
  private Object qualifier;


  /**
   * Creates a new policy qualifier containing a CPS URI.
   *
   * @param  cpsURI  URI to certificate policy statement.
   */
  public PolicyQualifierInfo(final String cpsURI)
  {
    if (cpsURI == null || "".equals(cpsURI)) {
      throw new IllegalArgumentException(
        "CPS URI cannot be null or empty string.");
    }
    qualifier = cpsURI;
    policyQualifierId = POLICY_QUALIFIER_ID_CPS;
  }


  /**
   * Creates a new policy qualifier containing a user notice.
   *
   * @param  notice  User notice.
   */
  public PolicyQualifierInfo(final UserNotice notice)
  {
    if (notice == null) {
      throw new IllegalArgumentException("User notice cannot be null.");
    }
    qualifier = notice;
    policyQualifierId = POLICY_QUALIFIER_ID_UNOTICE;
  }


  /** @return  Policy qualifier OID. */
  public String getPolicyQualifierId()
  {
    return policyQualifierId;
  }


  /**
   * @return  Policy qualifier, either a {@link UserNotice} or a {@link String}
   * containing the URI of the CPS.
   */
  public Object getQualifier()
  {
    return qualifier;
  }


  /**
   * @return  String indicating the type of qualifier followed by a string
   * representation of the qualifier.
   */
  @Override
  public String toString()
  {
    if (qualifier instanceof String) {
      return "CPS:" + qualifier;
    } else {
      return "UserNotice:" + qualifier.toString();
    }
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
      final PolicyQualifierInfo other = (PolicyQualifierInfo) obj;
      result = policyQualifierId.equals(other.getPolicyQualifierId()) &&
        qualifier.equals(other.getQualifier());
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    hash = HASH_FACTOR * hash + policyQualifierId.hashCode();
    hash = HASH_FACTOR * hash + qualifier.hashCode();
    return hash;
  }
}
