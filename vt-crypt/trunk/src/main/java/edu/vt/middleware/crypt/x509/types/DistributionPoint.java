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
 * Representation of the <code>DistributionPoint</code> type defined in section
 * 4.2.1.14 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DistributionPoint
{

  /** Hash code scale factor. */
  private static final int HASH_FACTOR = 31;

  /** Name of distribution point. */
  private Object distributionPoint;

  /** Reason flags supported by the distribution point. */
  private ReasonFlags reasons;

  /** Name of CRL issuer. */
  private GeneralNameList cRLIssuer;


  /**
   * Creates a new instance with no data. While RFC 2459 technically allows
   * this, it is meaningless to do so.
   */
  public DistributionPoint() {}


  /**
   * Creates a new instance where the distribution point is known by a name
   * relative to the CRL issuer name(s).
   *
   * @param  relativeName  Name relative to CRL issuer.
   * @param  flags  Revocation reasons supported by CRL distribution point.
   * @param  issuer  CRL issuer name(s); cannot be null in this case since this
   * is required to give a basis for the relative name.
   *
   * @throws  IllegalArgumentException  When issuer is null.
   */
  public DistributionPoint(
    final String relativeName,
    final ReasonFlags flags,
    final GeneralNameList issuer)
  {
    if (issuer == null) {
      throw new IllegalArgumentException(
        "CRL issuer name cannot be null when a relative name is used.");
    }
    distributionPoint = relativeName;
    reasons = flags;
    cRLIssuer = issuer;
  }


  /**
   * Creates a new instance with the given data.
   *
   * @param  names  Name(s) by which CRL distribution point is known.
   * @param  flags  Revocation reasons supported by CRL distribution point.
   * @param  issuer  CRL issuer name(s).
   */
  public DistributionPoint(
    final GeneralNameList names,
    final ReasonFlags flags,
    final GeneralNameList issuer)
  {
    distributionPoint = names;
    reasons = flags;
    cRLIssuer = issuer;
  }


  /**
   * @return  Name of the distribution point or null if no name is defined.
   * Returns a string in case the distribution point is simply a URI, otherwise
   * a {@link GeneralNameList} describing the name(s) by which the distribution
   * point is known.
   */
  public Object getDistributionPoint()
  {
    return distributionPoint;
  }


  /**
   * @return  Revocation reasons supported by the distribution point or null if
   * no reasons are defined.
   */
  public ReasonFlags getReasons()
  {
    return reasons;
  }


  /** @return  CRL issuer name(s) or null if none are defined. */
  public GeneralNameList getCRLIssuer()
  {
    return cRLIssuer;
  }


  /** @return  String representation of name of distribution point. */
  @Override
  public String toString()
  {
    if (distributionPoint != null) {
      return distributionPoint.toString();
    } else {
      return "Empty Distribution Point";
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
      final DistributionPoint other = (DistributionPoint) obj;
      result = (distributionPoint != null
        ? distributionPoint.equals(other.getDistributionPoint())
        : other.getDistributionPoint() == null) &&
        (reasons != null ? reasons.equals(other.getReasons())
                         : other.getReasons() == null) &&
        (cRLIssuer != null ? cRLIssuer.equals(other.getCRLIssuer())
                           : other.getCRLIssuer() == null);
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    if (distributionPoint != null) {
      hash = HASH_FACTOR * hash + distributionPoint.hashCode();
    }
    if (reasons != null) {
      hash = HASH_FACTOR * hash + reasons.hashCode();
    }
    if (cRLIssuer != null) {
      hash = HASH_FACTOR * hash + cRLIssuer.hashCode();
    }
    return hash;
  }
}
