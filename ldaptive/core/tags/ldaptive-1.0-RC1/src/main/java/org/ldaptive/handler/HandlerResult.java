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
package org.ldaptive.handler;

import org.ldaptive.LdapEntry;

/**
 * Handler result data.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class HandlerResult
{

  /** Ldap entry produced by a handler. */
  private final LdapEntry ldapEntry;

  /** Whether the search operation should be aborted. */
  private final boolean abortSearch;


  /**
   * Creates a new handler result.
   *
   * @param  entry  produced by a handler
   */
  public HandlerResult(final LdapEntry entry)
  {
    ldapEntry = entry;
    abortSearch = false;
  }


  /**
   * Creates a new handler result.
   *
   * @param  entry  produced by a handler
   * @param  abort  whether the search operation should be aborted
   */
  public HandlerResult(final LdapEntry entry, final boolean abort)
  {
    ldapEntry = entry;
    abortSearch = abort;
  }


  /**
   * Returns the ldap entry produced by a handler.
   *
   * @return  ldap entry
   */
  public LdapEntry getLdapEntry()
  {
    return ldapEntry;
  }


  /**
   * Returns whether the search operation should be aborted.
   *
   * @return  whether the search operation should be aborted
   */
  public boolean getAbortSearch()
  {
    return abortSearch;
  }
}
