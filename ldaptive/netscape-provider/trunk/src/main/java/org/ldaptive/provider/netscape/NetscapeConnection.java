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
package org.ldaptive.provider.netscape;

import javax.security.auth.callback.CallbackHandler;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPConstraints;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPResponse;
import netscape.ldap.LDAPResponseListener;
import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Request;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netscape provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Provider configuration. */
  private final NetscapeProviderConfig config;

  /** Operation time limit. */
  private int timeLimit;


  /**
   * Creates a new netscape ldap connection.
   *
   * @param  lc  ldap connection
   * @param  pc  provider configuration
   */
  public NetscapeConnection(
    final LDAPConnection lc, final NetscapeProviderConfig pc)
  {
    connection = lc;
    config = pc;
  }


  /**
   * Returns the operation time limit in milliseconds.
   *
   * @return  operation time limit
   */
  public int getTimeLimit()
  {
    return timeLimit;
  }


  /**
   * Sets the time limit.
   *
   * @param  limit  time in milliseconds
   */
  public void setTimeLimit(final int limit)
  {
    timeLimit = limit;
  }


  /**
   * Returns the underlying ldap connection.
   *
   * @return  ldap connection
   */
  public LDAPConnection getLdapConnection()
  {
    return connection;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    if (connection != null) {
      try {
        if (connection.isConnected()) {
          connection.disconnect();
        }
      } catch (LDAPException e) {
        logger.warn("Error closing connection", e);
      } finally {
        connection = null;
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> bind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response;
    if (request.getSaslConfig() != null) {
      response = saslBind(request);
    } else if (request.getDn() == null && request.getCredential() == null) {
      response = anonymousBind(request);
    } else {
      response = simpleBind(request);
    }
    return response;
  }


  /**
   * Creates a LDAP constraints from the supplied request.
   *
   * @param  request  to read properties from
   *
   * @return  ldap constraints
   */
  protected LDAPConstraints getLDAPConstraints(final Request request)
  {
    final LDAPConstraints cons = new LDAPConstraints();
    cons.setTimeLimit(timeLimit);
    cons.setServerControls(
      config.getControlProcessor().processRequestControls(
        request.getControls()));
    return cons;
  }


  /**
   * Performs an anonymous bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> anonymousBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final LDAPResponseListener listener = connection.bind(
        null,
        null,
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(r.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          r.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /**
   * Performs a simple bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> simpleBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final LDAPResponseListener listener = connection.bind(
        request.getDn(),
        request.getCredential().getString(),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(r.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          r.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /**
   * Performs a sasl bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> saslBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final SaslConfig sc = request.getSaslConfig();
      switch (sc.getMechanism()) {

      case EXTERNAL:
        connection.bind(
          null,
          new String[] {"EXTERNAL"},
          null,
          (CallbackHandler) null);
        break;

      case DIGEST_MD5:
        throw new UnsupportedOperationException("DIGEST-MD5 not supported");

      case CRAM_MD5:
        throw new UnsupportedOperationException("CRAM-MD5 not supported");

      case GSSAPI:
        throw new UnsupportedOperationException("GSSAPI not supported");

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " + sc.getMechanism());
      }
      response = new Response<Void>(null, ResultCode.SUCCESS);
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final NetscapeUtils util = new NetscapeUtils();
      final LDAPResponseListener listener = connection.add(
        new LDAPEntry(
          request.getDn(),
          util.fromLdapAttributes(request.getLdapAttributes())),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(r.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          r.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Boolean> compare(final CompareRequest request)
    throws LdapException
  {
    Response<Boolean> response = null;
    try {
      final NetscapeUtils util = new NetscapeUtils();
      final LDAPResponseListener listener = connection.compare(
        request.getDn(),
        util.fromLdapAttribute(request.getAttribute()),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = new Response<Boolean>(
        ResultCode.COMPARE_TRUE.value() == r.getResultCode(),
        ResultCode.valueOf(r.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          r.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> delete(final DeleteRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final LDAPResponseListener listener = connection.delete(
        request.getDn(),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(r.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          r.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modify(final ModifyRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final NetscapeUtils util = new NetscapeUtils();
      final LDAPResponseListener listener = connection.modify(
        request.getDn(),
        util.fromAttributeModification(request.getAttributeModifications()),
        (LDAPResponseListener) null,
        getLDAPConstraints(request));
      final LDAPResponse r = listener.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(r.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          r.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modifyDn(final ModifyDnRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final String[] dn = request.getNewDn().split(",", 2);
      connection.rename(
        request.getDn(),
        dn[0],
        dn[1],
        request.getDeleteOldRDn(),
        getLDAPConstraints(request));
      response = new Response<Void>(null, ResultCode.SUCCESS);
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getLDAPResultCode(),
        null,
        true);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final NetscapeSearchIterator i = new NetscapeSearchIterator(
      request, config);
    i.setTimeLimit(timeLimit);
    i.initialize(connection);
    return i;
  }
}
