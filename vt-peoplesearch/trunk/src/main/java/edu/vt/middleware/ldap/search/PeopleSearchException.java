/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.search;

/**
 * <code>PeopleSearchException</code> encapsulates the many exceptions that can
 * occur when searching a LDAP and managing XML.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class PeopleSearchException extends Exception
{

  /** serial version uid. */
  private static final long serialVersionUID = -6192833438629253038L;


  /** This creates a new <code>PeopleSearchException</code>. */
  public PeopleSearchException() {}


  /**
   * This creates a new <code>PeopleSearchException</code> with the supplied
   * <code>String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public PeopleSearchException(final String msg)
  {
    super(msg);
  }


  /**
   * This creates a new <code>PeopleSearchException</code> with the supplied
   * <code>Exception</code>.
   *
   * @param  e  <code>Exception</code>
   */
  public PeopleSearchException(final Exception e)
  {
    super(e);
  }
}
