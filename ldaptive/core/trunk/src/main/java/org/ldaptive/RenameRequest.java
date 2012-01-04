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

import java.util.Arrays;

/**
 * Contains the data required to perform an ldap rename operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RenameRequest extends AbstractRequest
{

  /** DN to rename. */
  private String oldRenameDn;

  /** New DN. */
  private String newRenameDn;


  /** Default constructor. */
  public RenameRequest() {}


  /**
   * Creates a new rename request.
   *
   * @param  oldDn  to rename
   * @param  newDn  to rename to
   */
  public RenameRequest(final String oldDn, final String newDn)
  {
    setDn(oldDn);
    setNewDn(newDn);
  }


  /**
   * Returns the DN to rename.
   *
   * @return  DN
   */
  public String getDn()
  {
    return oldRenameDn;
  }


  /**
   * Sets the DN to rename.
   *
   * @param  dn  to rename
   */
  public void setDn(final String dn)
  {
    oldRenameDn = dn;
  }


  /**
   * Returns the new DN.
   *
   * @return  DN
   */
  public String getNewDn()
  {
    return newRenameDn;
  }


  /**
   * Sets the new DN.
   *
   * @param  dn  to rename to
   */
  public void setNewDn(final String dn)
  {
    newRenameDn = dn;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::oldRenameDn=%s, newRenameDn=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        oldRenameDn,
        newRenameDn,
        Arrays.toString(getControls()));
  }
}
