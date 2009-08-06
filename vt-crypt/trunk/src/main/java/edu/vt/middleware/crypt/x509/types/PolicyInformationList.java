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
 * Encapsulates a collection of {@link PolicyInformation} objects as would
 * appear in Certificate Policies extension field of an X.509v3 certificate.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class PolicyInformationList extends AbstractList<PolicyInformation>
{
  /** Hash code seed value */
  private static final int HASH_SEED = 47;


  /**
   * Constructs a new instance from the given list of policies.
   *
   * @param  listOfPolicies List of certificate policies.
   */
  public PolicyInformationList(final List<PolicyInformation> listOfPolicies)
  {
    if (listOfPolicies == null) {
      throw new IllegalArgumentException("List of policies cannot be null.");
    }
    items = listOfPolicies.toArray(
      new PolicyInformation[listOfPolicies.size()]);
  }


  /**
   * Constructs a new instance from the given array of policies.
   *
   * @param  arrayOfPolicies  Array of policies.
   */
  public PolicyInformationList(final PolicyInformation[] arrayOfPolicies)
  {
    if (arrayOfPolicies == null) {
      throw new IllegalArgumentException("Array of policies cannot be null.");
    }
    items = arrayOfPolicies;
  }


  /** {@inheritDoc} */
  @Override
  protected int getHashSeed()
  {
    return HASH_SEED;
  }
}
