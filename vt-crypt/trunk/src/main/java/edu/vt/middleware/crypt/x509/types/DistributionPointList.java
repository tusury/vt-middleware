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

import java.util.List;

/**
 * Representation of the SEQUENCE of <code>DistributionPoint</code> types that
 * are the value of the <code>CRLDistributionPoints</code> extension field
 * described in section 4.2.1.14 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class DistributionPointList extends AbstractList<DistributionPoint>
{
  /**
   * Constructs a new instance from the given list of distribution points.
   *
   * @param  listOfDistPoints List of distribution points.
   */
  public DistributionPointList(final List<DistributionPoint> listOfDistPoints)
  {
    if (listOfDistPoints == null) {
      throw new IllegalArgumentException(
        "List of distribution points cannot be null.");
    }
    items = listOfDistPoints.toArray(
      new DistributionPoint[listOfDistPoints.size()]);
  }


  /**
   * Constructs a new instance from the given array of distribution points.
   *
   * @param  arrayOfDistPoints  Array of distribution points.
   */
  public DistributionPointList(final DistributionPoint[] arrayOfDistPoints)
  {
    if (arrayOfDistPoints == null) {
      throw new IllegalArgumentException(
        "Array of distribution points cannot be null.");
    }
    items = arrayOfDistPoints;
  }
}
