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

import java.util.HashSet;
import java.util.Set;
import org.ldaptive.Connection;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.LdapUtils;
import org.ldaptive.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for entry handlers which simply returns values unaltered.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLdapEntryHandler implements LdapEntryHandler
{

  /** Log for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public HandlerResult process(
    final Connection conn,
    final SearchRequest request,
    final LdapEntry entry)
    throws LdapException
  {
    if (entry != null) {
      entry.setDn(processDn(conn, request, entry));
      processAttributes(conn, request, entry);
    }
    return new HandlerResult(entry);
  }


  /**
   * Process the dn of an ldap entry.
   *
   * @param  conn  the search was performed on
   * @param  request  used to find the ldap entry
   * @param  entry  ldap entry to extract the dn from
   *
   * @return  processed dn
   */
  protected String processDn(
    final Connection conn,
    final SearchRequest request,
    final LdapEntry entry)
  {
    return entry.getDn();
  }


  /**
   * Process the attributes of an ldap entry.
   *
   * @param  conn  the search was performed on
   * @param  request  used to find the ldap entry
   * @param  entry  ldap entry to extract the attributes from
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  protected void processAttributes(
    final Connection conn,
    final SearchRequest request,
    final LdapEntry entry)
    throws LdapException
  {
    for (LdapAttribute la : entry.getAttributes()) {
      processAttribute(conn, request, la);
    }
  }


  /**
   * Process a single attribute.
   *
   * @param  conn  the search was performed on
   * @param  request  used to find the ldap entry
   * @param  attr  to process
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  protected void processAttribute(
    final Connection conn,
    final SearchRequest request,
    final LdapAttribute attr)
    throws LdapException
  {
    if (attr != null) {
      attr.setName(processAttributeName(conn, request, attr.getName()));
      if (attr.isBinary()) {
        final Set<byte[]> newValues = new HashSet<byte[]>(attr.size());
        for (byte[] b : attr.getBinaryValues()) {
          newValues.add(processAttributeValue(conn, request, b));
        }
        attr.clear();
        attr.addBinaryValues(newValues);
      } else {
        final Set<String> newValues = new HashSet<String>(attr.size());
        for (String s : attr.getStringValues()) {
          newValues.add(processAttributeValue(conn, request, s));
        }
        attr.clear();
        attr.addStringValues(newValues);
      }
    }
  }


  /**
   * Returns the supplied attribute name unaltered.
   *
   * @param  conn  the search was performed on
   * @param  request  used to find the ldap entry
   * @param  name  to process
   *
   * @return  processed name
   */
  protected String processAttributeName(
    final Connection conn,
    final SearchRequest request,
    final String name)
  {
    return name;
  }


  /**
   * Returns the supplied attribute value unaltered.
   *
   * @param  conn  the search was performed on
   * @param  request  used to find the ldap entry
   * @param  value  to process
   *
   * @return  processed value
   */
  protected String processAttributeValue(
    final Connection conn,
    final SearchRequest request,
    final String value)
  {
    return value;
  }


  /**
   * Returns the supplied attribute value unaltered.
   *
   * @param  conn  the search was performed on
   * @param  request  used to find the ldap entry
   * @param  value  to process
   *
   * @return  processed value
   */
  protected byte[] processAttributeValue(
    final Connection conn,
    final SearchRequest request,
    final byte[] value)
  {
    return value;
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o)
  {
    return LdapUtils.areEqual(this, o);
  }


  /** {@inheritDoc} */
  @Override
  public abstract int hashCode();
}
