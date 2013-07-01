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
package org.ldaptive;

import org.ldaptive.control.RequestControl;
import org.ldaptive.handler.IntermediateResponseHandler;

/**
 * Marker interface for all ldap requests.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Request extends Message<RequestControl>
{


  /**
   * Returns whether to follow referrals.
   *
   * @return  whether to follow referrals
   */
  boolean getFollowReferrals();


  /**
   * Returns the intermediate response handlers.
   *
   * @return  intermediate response handlers
   */
  IntermediateResponseHandler[] getIntermediateResponseHandlers();
}
