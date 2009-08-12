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

import java.util.Arrays;

/**
 * Representation of the <code>PolicyInformation</code> type defined in
 * section 4.2.1.5 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PolicyInformation
{
  /** Hash code scale factor */
  private static final int HASH_FACTOR = 31;

  /** Policy OID */
  private String policyIdentifier;

  /** Optional policy qualifiers */
  private PolicyQualifierInfo[] policyQualifiers;



  /**
   * Creates a new instance with the given OID and no qualifiers defined.
   *
   * @param  oid  Policy information OID.
   */
  public PolicyInformation(final String oid)
  {
    this(oid, null);
  }


  /**
   * Creates a new instance with the given OID and qualifiers.
   *
   * @param  oid  Policy information OID.
   * @param  qualifiers  Array of policy qualifiers.
   */
  public PolicyInformation(
    final String oid,
    final PolicyQualifierInfo[] qualifiers)
  {
    if (oid == null || "".equals(oid)) {
      throw new IllegalArgumentException(
        "Policy information OID cannot be null or empty string.");
    }
    policyIdentifier = oid;
    policyQualifiers = qualifiers;
  }


  /**
   * @return  Gets the OID that identifies the policy.
   */
  public String getPolicyIdentifier()
  {
    return policyIdentifier;
  }


  /**
   * @return  Array of policy qualifiers or null if no policy qualifiers
   * are defined.
   */
  public PolicyQualifierInfo[] getPolicyQualifiers()
  {
    return policyQualifiers;
  }



  /**
   * @return  Policy OID string followed by the policy qualifiers, if any,
   * formatted as a string.
   */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(policyIdentifier);
    if (policyQualifiers != null) {
      sb.append(":[");
      for (PolicyQualifierInfo info : policyQualifiers) {
        sb.append(info);
      }
      sb.append(']');
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
      final PolicyInformation other = (PolicyInformation) obj;
      result =
        policyIdentifier.equals(other.getPolicyIdentifier()) &&
        (policyQualifiers != null
          ? Arrays.equals(policyQualifiers, other.getPolicyQualifiers())
          : other.getPolicyQualifiers() == null);
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    hash = HASH_FACTOR * hash + policyIdentifier.hashCode();
    if (policyQualifiers != null) {
      hash = HASH_FACTOR * hash + Arrays.hashCode(policyQualifiers);
    }
    return hash;
  }
}
