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
package org.ldaptive.auth;

import java.util.Arrays;
import org.ldaptive.Connection;
import org.ldaptive.Credential;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality to authenticate users against an ldap directory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Authenticator
{

  /** NoOp entry resolver. */
  private static final EntryResolver NOOP_RESOLVER = new NoOpEntryResolver();

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** For finding user DNs. */
  private DnResolver dnResolver;

  /** Handler to process authentication. */
  private AuthenticationHandler authenticationHandler;

  /** For finding user entries. */
  private EntryResolver entryResolver;

  /** Handlers to process authentication responses. */
  private AuthenticationResponseHandler[] authenticationResponseHandlers;


  /** Default constructor. */
  public Authenticator() {}


  /**
   * Creates a new authenticator.
   *
   * @param  resolver  dn resolver
   * @param  handler  authentication handler
   */
  public Authenticator(
    final DnResolver resolver,
    final AuthenticationHandler handler)
  {
    setDnResolver(resolver);
    setAuthenticationHandler(handler);
  }


  /**
   * Returns the DN resolver.
   *
   * @return  DN resolver
   */
  public DnResolver getDnResolver()
  {
    return dnResolver;
  }


  /**
   * Sets the DN resolver.
   *
   * @param  resolver  for finding DNs
   */
  public void setDnResolver(final DnResolver resolver)
  {
    dnResolver = resolver;
  }


  /**
   * Returns the authentication handler.
   *
   * @return  authentication handler
   */
  public AuthenticationHandler getAuthenticationHandler()
  {
    return authenticationHandler;
  }


  /**
   * Sets the authentication handler.
   *
   * @param  handler  for performing authentication
   */
  public void setAuthenticationHandler(final AuthenticationHandler handler)
  {
    authenticationHandler = handler;
  }


  /**
   * Returns the entry resolver.
   *
   * @return  entry resolver
   */
  public EntryResolver getEntryResolver()
  {
    return entryResolver;
  }


  /**
   * Sets the entry resolver.
   *
   * @param  resolver  for finding entries
   */
  public void setEntryResolver(final EntryResolver resolver)
  {
    entryResolver = resolver;
  }


  /**
   * Returns the authentication response handlers.
   *
   * @return  authentication response handlers
   */
  public AuthenticationResponseHandler[] getAuthenticationResponseHandlers()
  {
    return authenticationResponseHandlers;
  }


  /**
   * Sets the authentication response handlers.
   *
   * @param  handlers  authentication response handlers
   */
  public void setAuthenticationResponseHandlers(
    final AuthenticationResponseHandler... handlers)
  {
    authenticationResponseHandlers = handlers;
  }


  /**
   * This will attempt to find the DN for the supplied user. {@link
   * DnResolver#resolve(String)} is invoked to perform this operation.
   *
   * @param  user  to find DN for
   *
   * @return  user DN
   *
   * @throws  LdapException  if an LDAP error occurs during resolution
   */
  public String resolveDn(final String user)
    throws LdapException
  {
    return dnResolver.resolve(user);
  }


  /**
   * Authenticate the user in the supplied request.
   *
   * @param  request  authentication request
   *
   * @return  response containing the ldap entry of the user authenticated
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  public AuthenticationResponse authenticate(
    final AuthenticationRequest request)
    throws LdapException
  {
    return authenticate(resolveDn(request.getUser()), request);
  }


  /**
   * Performs authentication by opening a new connection to the LDAP and binding
   * as the supplied DN. If return attributes have been request, the user entry
   * will be searched on the same connection.
   *
   * @param  dn  to authenticate as
   * @param  request  containing authentication parameters
   *
   * @return  ldap entry for the supplied DN
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  protected AuthenticationResponse authenticate(
    final String dn,
    final AuthenticationRequest request)
    throws LdapException
  {
    logger.debug("authenticate dn={} with request={}", dn, request);

    // check the credential
    final Credential credential = request.getCredential();
    if (credential == null || credential.getBytes() == null) {
      throw new IllegalArgumentException(
        "Cannot authenticate dn, credential cannot be null");
    }
    if (credential.getBytes().length == 0) {
      throw new IllegalArgumentException(
        "Cannot authenticate dn, credential cannot be empty");
    }

    // check the dn
    if (dn == null || "".equals(dn)) {
      throw new IllegalArgumentException(
        "Cannot authenticate dn, dn cannot be empty or null");
    }

    LdapEntry entry = null;

    AuthenticationHandlerResponse response = null;
    try {
      final AuthenticationCriteria ac = new AuthenticationCriteria(dn);
      ac.setCredential(request.getCredential());

      // attempt to bind as this dn
      response = getAuthenticationHandler().authenticate(ac);
      // resolve the entry
      entry = resolveEntry(request, response.getConnection(), ac);
    } finally {
      if (response != null && response.getConnection() != null) {
        response.getConnection().close();
      }
    }

    logger.info(
      "Authentication {} for dn: {}",
      response.getResult() ? "succeeded" : "failed",
      dn);

    final AuthenticationResponse authResponse = new AuthenticationResponse(
      response.getResult(),
      response.getResultCode(),
      entry,
      response.getMessage(),
      response.getControls(),
      response.getMessageId());

    // execute authentication response handlers
    if (
      getAuthenticationResponseHandlers() != null &&
        getAuthenticationResponseHandlers().length > 0) {
      for (AuthenticationResponseHandler ah :
           getAuthenticationResponseHandlers()) {
        ah.process(authResponse);
      }
    }

    logger.debug(
      "authenticate response={} for dn={} with request={}",
      new Object[] {response, dn, request});
    return authResponse;
  }


  /**
   * Attempts to find the ldap entry for the supplied DN. If the supplied
   * connection is null, a {@link NoOpEntryResolver} is used. If an entry
   * resolver has been provided it is used, otherwise a {@link
   * SearchEntryResolver} is used if return attributes have been requested.
   *
   * @param  request  authentication request
   * @param  conn  that authentication occurred on
   * @param  criteria  needed by the entry resolver
   *
   * @return  ldap entry
   *
   * @throws  LdapException  if an error occurs resolving the entry
   */
  protected LdapEntry resolveEntry(
    final AuthenticationRequest request,
    final Connection conn,
    final AuthenticationCriteria criteria)
    throws LdapException
  {
    LdapEntry entry;
    if (conn == null) {
      entry = NOOP_RESOLVER.resolve(conn, criteria);
    } else if (entryResolver != null) {
      entry = entryResolver.resolve(conn, criteria);
    } else {
      if (
        request.getReturnAttributes() == null ||
          request.getReturnAttributes().length > 0) {
        final EntryResolver er = new SearchEntryResolver(
          request.getReturnAttributes());
        entry = er.resolve(conn, criteria);
      } else {
        entry = NOOP_RESOLVER.resolve(conn, criteria);
      }
    }
    return entry;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::dnResolver=%s, authenticationHandler=%s, " +
        "entryResolver=%s, authenticationResponseHandlers=%s]",
        getClass().getName(),
        hashCode(),
        getDnResolver(),
        getAuthenticationHandler(),
        getEntryResolver(),
        Arrays.toString(getAuthenticationResponseHandlers()));
  }
}
