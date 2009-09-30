/*
  $Id: DistributionPointList.java 428 2009-08-12 18:12:49Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 428 $
  Updated: $Date: 2009-08-12 14:12:49 -0400 (Wed, 12 Aug 2009) $
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.List;

/**
 * Representation of the SEQUENCE of <code>DistributionPoint</code> types that
 * are the value of the <code>CRLDistributionPoints</code> extension field
 * described in section 4.2.1.14 of RFC 2459.
 *
 * @author Middleware
 * @version $Revision: 428 $
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
