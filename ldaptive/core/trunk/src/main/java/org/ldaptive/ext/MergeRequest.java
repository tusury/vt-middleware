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
package org.ldaptive.ext;

import java.util.Arrays;
import org.ldaptive.AbstractRequest;
import org.ldaptive.LdapEntry;

/**
 * Contains the data required to perform a merge operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class MergeRequest extends AbstractRequest
{

  /** Ldap entry to merge. */
  private LdapEntry ldapEntry;

  /** Whether to delete the entry. */
  private boolean deleteEntry;

  /** Attribute names to ignore when determining merge. */
  private String[] ignoreAttrs;


  /** Default constructor. */
  public MergeRequest() {}


  /**
   * Creates a new merge request.
   *
   * @param  entry  to merge into the LDAP
   */
  public MergeRequest(final LdapEntry entry)
  {
    setEntry(entry);
  }


  /**
   * Creates a new merge request.
   *
   * @param  entry  to merge into the LDAP
   * @param  delete  whether the supplied entry should be deleted
   */
  public MergeRequest(final LdapEntry entry, final boolean delete)
  {
    setEntry(entry);
    setDeleteEntry(delete);
  }


  /**
   * Returns the ldap entry to merge.
   *
   * @return  ldap entry to merge
   */
  public LdapEntry getEntry()
  {
    return ldapEntry;
  }


  /**
   * Sets the ldap entry to merge into the LDAP.
   *
   * @param  entry  to merge
   */
  public void setEntry(final LdapEntry entry)
  {
    ldapEntry = entry;
  }


  /**
   * Returns whether to delete the entry.
   *
   * @return  whether to delete the entry
   */
  public boolean getDeleteEntry()
  {
    return deleteEntry;
  }


  /**
   * Sets whether to delete the entry.
   *
   * @param  b  whether to delete the entry
   */
  public void setDeleteEntry(final boolean b)
  {
    deleteEntry = b;
  }


  /**
   * Returns the names of attributes that are ignored when determining whether a
   * modify should occur.
   *
   * @return  attribute names to ignore
   */
  public String[] getIgnoreAttributes()
  {
    return ignoreAttrs;
  }


  /**
   * Sets the list of attribute names to ignore when determining whether a
   * modify should occur.
   *
   * @param  attrs  names to ignore
   */
  public void setIgnoreAttributes(final String... attrs)
  {
    ignoreAttrs = attrs;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::ldapEntry=%s, deleteEntry=%s, ignoreAttributes=%s, " +
        "controls=%s]",
        getClass().getName(),
        hashCode(),
        ldapEntry,
        deleteEntry,
        Arrays.toString(ignoreAttrs),
        Arrays.toString(getControls()));
  }
}
