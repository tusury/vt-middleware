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

/**
 * Enum to define how referrals should be handled.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public enum ReferralBehavior {

  /** follow referrals. */
  FOLLOW,

  /** throw exception no referral. */
  THROW,

  /** ignore referrals. */
  IGNORE;
}
