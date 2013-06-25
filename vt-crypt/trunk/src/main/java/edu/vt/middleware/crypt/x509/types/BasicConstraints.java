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
 * Representation of the <code>BasicConstraints</code> type defined in section
 * 4.2.1.10 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BasicConstraints
{

  /** Hash code scale factor. */
  private static final int HASH_FACTOR = 31;

  /** Whether or not this is a CA certificate. */
  private boolean cA;

  /** Optional maximum path length of certificate chain. */
  private Integer pathLengthConstraint;


  /**
   * Creates a new instance with no path length constraint.
   *
   * @param  isCA  Whether or not this is a CA certificate.
   */
  public BasicConstraints(final boolean isCA)
  {
    cA = isCA;
  }


  /**
   * Creates a new instance with the given path length constraint.
   *
   * @param  isCA  Whether or not this is a CA certificate.
   * @param  maxPathLength  Maximum number of CA certificates that may follow
   * this one.
   */
  public BasicConstraints(final boolean isCA, final int maxPathLength)
  {
    cA = isCA;
    pathLengthConstraint = maxPathLength;
  }


  /**
   * @return  True if the basic constraint identifies itself as a CA
   * certificate, false otherwise. Note that RFC 2459 indicates this field MUST
   * BE true for a CA certificate, but that the extension SHOULD NOT be present
   * for an end-user certificate.
   */
  public boolean isCA()
  {
    return cA;
  }


  /**
   * @return  The maximum number of CA certificates that may follow this one in
   * the certificate chain or null if no path length constraints are defined.
   */
  public Integer getPathLengthConstraint()
  {
    return pathLengthConstraint;
  }


  /**
   * @return  String representation containing the CA and PathLengthConstraint
   * fields.
   */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("CA:");
    sb.append(cA);
    if (pathLengthConstraint != null) {
      sb.append(", ");
      sb.append(pathLengthConstraint);
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
      final BasicConstraints other = (BasicConstraints) obj;
      result = cA == other.isCA() &&
        (pathLengthConstraint != null
          ? pathLengthConstraint.equals(other.getPathLengthConstraint())
          : other.getPathLengthConstraint() == null);
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    hash = HASH_FACTOR * hash + (cA ? 1 : 0);
    if (pathLengthConstraint != null) {
      hash = HASH_FACTOR * hash + pathLengthConstraint.hashCode();
    }
    return hash;
  }
}
