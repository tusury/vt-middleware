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

import org.ldaptive.control.ResponseControl;

/**
 * Exception thrown when an ldap operation attempt fails.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class OperationException extends LdapException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 4995760197708755601L;


  /**
   * Creates a new operation exception.
   *
   * @param  msg  describing this exception
   */
  public OperationException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  msg  describing this exception
   * @param  code  result code
   */
  public OperationException(final String msg, final ResultCode code)
  {
    super(msg, code);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  msg  describing this exception
   * @param  code  result code
   * @param  c  response controls
   */
  public OperationException(
    final String msg,
    final ResultCode code,
    final ResponseControl[] c)
  {
    super(msg, code, c);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  e  provider specific exception
   */
  public OperationException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  e  provider specific exception
   * @param  code  result code
   */
  public OperationException(final Exception e, final ResultCode code)
  {
    super(e, code);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  e  provider specific exception
   * @param  code  result code
   * @param  c  response controls
   */
  public OperationException(
    final Exception e,
    final ResultCode code,
    final ResponseControl[] c)
  {
    super(e, code, c);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   */
  public OperationException(final String msg, final Exception e)
  {
    super(msg, e);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  code  result code
   */
  public OperationException(
    final String msg,
    final Exception e,
    final ResultCode code)
  {
    super(msg, e, code);
  }


  /**
   * Creates a new operation exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  code  result code
   * @param  c  response controls
   */
  public OperationException(
    final String msg,
    final Exception e,
    final ResultCode code,
    final ResponseControl[] c)
  {
    super(msg, e, code, c);
  }
}
