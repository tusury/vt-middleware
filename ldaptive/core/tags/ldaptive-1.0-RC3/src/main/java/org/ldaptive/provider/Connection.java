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
package org.ldaptive.provider;

import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Response;
import org.ldaptive.SearchRequest;

/**
 * Interface for a provider specific implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Connection
{


  /**
   * Bind to the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the bind operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> bind(BindRequest request)
    throws LdapException;


  /**
   * Add an entry to an ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the add operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> add(AddRequest request)
    throws LdapException;


  /**
   * Compare an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the compare operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Boolean> compare(CompareRequest request)
    throws LdapException;


  /**
   * Delete an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the delete operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> delete(DeleteRequest request)
    throws LdapException;


  /**
   * Modify an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the modify operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> modify(ModifyRequest request)
    throws LdapException;


  /**
   * Modify the DN of an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the modify dn operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> modifyDn(ModifyDnRequest request)
    throws LdapException;


  /**
   * Search the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  search iterator
   *
   * @throws  LdapException  if an error occurs
   */
  SearchIterator search(SearchRequest request)
    throws LdapException;


  /**
   * Tear down this connection to an LDAP.
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  void close()
    throws LdapException;
}
