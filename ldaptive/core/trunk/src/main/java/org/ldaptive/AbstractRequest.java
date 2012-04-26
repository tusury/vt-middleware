/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import org.ldaptive.control.RequestControl;

/**
 * Contains the data common to all request objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractRequest implements Request
{

  /** request controls. */
  private RequestControl[] controls;

  /** Whether to follow referrals. */
  private boolean followReferrals;


  /** {@inheritDoc} */
  @Override
  public RequestControl[] getControls()
  {
    return controls;
  }


  /**
   * Sets the controls for this request.
   *
   * @param  c  controls to set
   */
  public void setControls(final RequestControl... c)
  {
    controls = c;
  }


  /** {@inheritDoc} */
  @Override
  public boolean getFollowReferrals()
  {
    return followReferrals;
  }


  /**
   * Sets whether to follow referrals.
   *
   * @param  b  whether to follow referrals
   */
  public void setFollowReferrals(final boolean b)
  {
    followReferrals = b;
  }
}
