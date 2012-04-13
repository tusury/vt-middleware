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
package org.ldaptive.provider.jldap;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.LDAPResponseQueue;
import com.novell.security.sasl.RealmCallback;
import com.novell.security.sasl.RealmChoiceCallback;
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
import org.ldaptive.SearchRequest;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JLDAP provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JLdapConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Provider configuration. */
  private final JLdapProviderConfig config;


  /**
   * Creates a new jldap connection.
   *
   * @param  conn  ldap connection
   * @param  pc  provider configuration
   */
  public JLdapConnection(
    final LDAPConnection conn, final JLdapProviderConfig pc)
  {
    connection = conn;
    config = pc;
  }


  /**
   * Returns the underlying ldap connection.
   *
   * @return  ldap connection
   */
  public LDAPConnection getLDAPConnection()
  {
    return connection;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    try {
      if (connection != null) {
        connection.disconnect();
      }
    } catch (LDAPException e) {
      throw new LdapException(e, ResultCode.valueOf(e.getResultCode()));
    } finally {
      connection = null;
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
      final LDAPResponseQueue queue = connection.bind(
        LDAPConnection.LDAP_V3,
        (String) null,
        (byte[]) null,
        (LDAPResponseQueue) null,
        getLDAPConstraints(request));
      final LDAPResponse lr = (LDAPResponse) queue.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(lr.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          lr.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
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
      final LDAPResponseQueue queue = connection.bind(
        LDAPConnection.LDAP_V3,
        request.getDn(),
        request.getCredential().getBytes(),
        (LDAPResponseQueue) null,
        getLDAPConstraints(request));
      final LDAPResponse lr = (LDAPResponse) queue.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(lr.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          lr.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
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
    try {
      final SaslConfig sc = request.getSaslConfig();
      switch (sc.getMechanism()) {

      case EXTERNAL:
        throw new UnsupportedOperationException("SASL External not supported");
        /* current implementation appears to be broken
         * see http://tinyurl.com/7ojdzlz
         * connection.bind(
         * (String) null,
         * sc.getAuthorizationId(),
         * new String[] {"EXTERNAL"},
         * null,
         * (Object) null);
         * break;
         */

      case DIGEST_MD5:
        connection.bind(
          (String) null,
          request.getDn(),
          new String[] {"DIGEST-MD5"},
          null,
          new SaslCallbackHandler(
            null,
            request.getCredential() != null
              ? request.getCredential().getString() : null));
        break;

      case CRAM_MD5:
        throw new UnsupportedOperationException("CRAM-MD5 not supported");

      case GSSAPI:
        throw new UnsupportedOperationException("GSSAPI not supported");

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " + sc.getMechanism());
      }
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
        null,
        true);
    }
    return new Response<Void>(null, ResultCode.SUCCESS, null);
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final JLdapUtils bu = new JLdapUtils();
      final LDAPResponseQueue queue = connection.add(
        new LDAPEntry(
          request.getDn(),
          bu.fromLdapAttributes(request.getLdapAttributes())),
        (LDAPResponseQueue) null,
        getLDAPConstraints(request));
      final LDAPResponse lr = (LDAPResponse) queue.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(lr.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          lr.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
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
      final JLdapUtils bu = new JLdapUtils();
      final LDAPResponseQueue queue = connection.compare(
        request.getDn(),
        bu.fromLdapAttribute(request.getAttribute()),
        (LDAPResponseQueue) null,
        getLDAPConstraints(request));
      final LDAPResponse lr = (LDAPResponse) queue.getResponse();
      response = new Response<Boolean>(
        lr.getResultCode() == ResultCode.COMPARE_TRUE.value(),
        ResultCode.valueOf(lr.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          lr.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
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
      final LDAPResponseQueue queue = connection.delete(
        request.getDn(),
        (LDAPResponseQueue) null,
        getLDAPConstraints(request));
      final LDAPResponse lr = (LDAPResponse) queue.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(lr.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          lr.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
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
      final JLdapUtils bu = new JLdapUtils();
      final LDAPResponseQueue queue = connection.modify(
        request.getDn(),
        bu.fromAttributeModification(request.getAttributeModifications()),
        (LDAPResponseQueue) null,
        getLDAPConstraints(request));
      final LDAPResponse lr = (LDAPResponse) queue.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(lr.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          lr.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
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
      final LDAPResponseQueue queue = connection.rename(
        request.getDn(),
        dn[0],
        dn[1],
        request.getDeleteOldRDn(),
        (LDAPResponseQueue) null,
        getLDAPConstraints(request));
      final LDAPResponse lr = (LDAPResponse) queue.getResponse();
      response = new Response<Void>(
        null,
        ResultCode.valueOf(lr.getResultCode()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          lr.getControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode(),
        null,
        true);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(final SearchRequest request)
    throws LdapException
  {
    final JLdapSearchIterator i = new JLdapSearchIterator(request, config);
    i.initialize(connection);
    return i;
  }


  /**
   * Returns an ldap constraints object configured with the supplied request.
   *
   * @param  request  request containing configuration to create constraints
   *
   * @return  ldap constraints
   */
  protected LDAPConstraints getLDAPConstraints(final Request request)
  {
    final LDAPConstraints constraints = new LDAPConstraints();
    if (request.getControls() != null) {
      constraints.setControls(
        config.getControlProcessor().processRequestControls(
          request.getControls()));
    }
    return constraints;
  }


  /** Callback handler used by SASL mechanisms. */
  private static class SaslCallbackHandler implements CallbackHandler
  {

    /** user name. */
    private final String user;

    /** password. */
    private final char[] pass;


    /**
     * Creates a new bind callback handler.
     *
     * @param  u  username to bind with
     * @param  p  password to bind with
     */
    public SaslCallbackHandler(final String u, final String p)
    {
      user = u;
      if (p != null) {
        pass = p.toCharArray();
      } else {
        pass = null;
      }
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Callback[] callbacks)
      throws IOException, UnsupportedCallbackException
    {
      for (Callback cb : callbacks) {
        if (cb instanceof NameCallback) {
          // if user is null, the authzId will be used as it's the default name
          ((NameCallback) cb).setName(
            user != null ? user : ((NameCallback) cb).getDefaultName());
        } else if (cb instanceof PasswordCallback) {
          ((PasswordCallback) cb).setPassword(pass);
        } else if (cb instanceof RealmCallback) {
          ((RealmCallback) cb).setText(((RealmCallback) cb).getDefaultText());
        } else if (cb instanceof RealmChoiceCallback) {
          ((RealmChoiceCallback) cb).setSelectedIndex(0);
        }
      }
    }
  }
}
