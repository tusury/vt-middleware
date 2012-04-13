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
package org.ldaptive.provider.unboundid;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.CRAMMD5BindRequest;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.EXTERNALBindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequest;
import com.unboundid.ldap.sdk.GSSAPIBindRequestProperties;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SASLBindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ProviderUtils;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.sasl.DigestMd5Config;
import org.ldaptive.sasl.GssApiConfig;
import org.ldaptive.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unbound ID provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIDConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Provider configuration. */
  private final UnboundIDProviderConfig config;


  /**
   * Creates a new unboundid ldap connection.
   *
   * @param  lc  ldap connection
   * @param  pc  provider configuration
   */
  public UnboundIDConnection(
    final LDAPConnection lc, final UnboundIDProviderConfig pc)
  {
    connection = lc;
    config = pc;
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
        connection.close();
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
      SimpleBindRequest sbr;
      if (request.getControls() != null) {
        sbr = new SimpleBindRequest(
          "",
          new byte[0],
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      } else {
        sbr = new SimpleBindRequest();
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
      SimpleBindRequest sbr;
      if (request.getControls() != null) {
        sbr = new SimpleBindRequest(
          new DN(request.getDn()),
          request.getCredential().getBytes(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      } else {
        sbr = new SimpleBindRequest(
          request.getDn(),
          request.getCredential().getBytes());
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
      SASLBindRequest sbr;
      final SaslConfig sc = request.getSaslConfig();
      switch (sc.getMechanism()) {

      case EXTERNAL:
        sbr = new EXTERNALBindRequest(
          sc.getAuthorizationId(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
         break;

      case DIGEST_MD5:

        String realm = sc instanceof DigestMd5Config
          ? ((DigestMd5Config) sc).getRealm() : null;
        if (realm == null && request.getDn().contains("@")) {
          realm = request.getDn().substring(request.getDn().indexOf("@") + 1);
        }
        sbr = new DIGESTMD5BindRequest(
          request.getDn(),
          "".equals(sc.getAuthorizationId()) ? null : sc.getAuthorizationId(),
          request.getCredential() != null ? request.getCredential().getBytes()
                                          : null,
          realm,
          config.getControlProcessor().processRequestControls(
            request.getControls()));
        break;

      case CRAM_MD5:
        sbr = new CRAMMD5BindRequest(
          request.getDn(),
          request.getCredential() != null ? request.getCredential().getBytes()
                                          : null,
          config.getControlProcessor().processRequestControls(
            request.getControls()));
        break;

      case GSSAPI:

        final GSSAPIBindRequestProperties props =
          new GSSAPIBindRequestProperties(
            request.getDn(),
            request.getCredential() != null ? request.getCredential()
              .getBytes() : null);
        props.setAuthorizationID(sc.getAuthorizationId());
        props.setRealm(
          sc instanceof GssApiConfig ? ((GssApiConfig) sc).getRealm()
                                         : null);
        sbr = new GSSAPIBindRequest(
          props,
          config.getControlProcessor().processRequestControls(
            request.getControls()));
        break;

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " + sc.getMechanism());
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
      final UnboundIDUtils util = new UnboundIDUtils();
      final com.unboundid.ldap.sdk.AddRequest ar =
        new com.unboundid.ldap.sdk.AddRequest(
          new DN(request.getDn()),
          util.fromLdapAttributes(request.getLdapAttributes()),
          config.getControlProcessor().processRequestControls(
            request.getControls()));

      final LDAPResult result = connection.add(ar);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
      com.unboundid.ldap.sdk.CompareRequest cr;
      if (request.getAttribute().isBinary()) {
        cr = new com.unboundid.ldap.sdk.CompareRequest(
          new DN(request.getDn()),
          request.getAttribute().getName(),
          request.getAttribute().getBinaryValue(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      } else {
        cr = new com.unboundid.ldap.sdk.CompareRequest(
          new DN(request.getDn()),
          request.getAttribute().getName(),
          request.getAttribute().getStringValue(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }

      final CompareResult result = connection.compare(cr);
      response = new Response<Boolean>(
        result.compareMatched(),
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
      final com.unboundid.ldap.sdk.DeleteRequest dr =
        new com.unboundid.ldap.sdk.DeleteRequest(
          new DN(request.getDn()),
          config.getControlProcessor().processRequestControls(
            request.getControls()));

      final LDAPResult result = connection.delete(dr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
      final UnboundIDUtils bu = new UnboundIDUtils();
      final com.unboundid.ldap.sdk.ModifyRequest mr =
        new com.unboundid.ldap.sdk.ModifyRequest(
          new DN(request.getDn()),
          bu.fromAttributeModification(request.getAttributeModifications()),
          config.getControlProcessor().processRequestControls(
            request.getControls()));

      final LDAPResult result = connection.modify(mr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
      final DN dn = new DN(request.getDn());
      final DN newDn = new DN(request.getNewDn());
      final com.unboundid.ldap.sdk.ModifyDNRequest mdr =
        new com.unboundid.ldap.sdk.ModifyDNRequest(
          dn,
          newDn.getRDN(),
          request.getDeleteOldRDn(),
          newDn.getParent(),
          config.getControlProcessor().processRequestControls(
            request.getControls()));

      final LDAPResult result = connection.modifyDN(mdr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        config.getControlProcessor().processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      ProviderUtils.throwOperationException(
        config.getOperationRetryResultCodes(),
        e,
        e.getResultCode().intValue(),
        config.getControlProcessor().processResponseControls(
          request.getControls(), e.getResponseControls()),
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
    final UnboundIDSearchIterator i = new UnboundIDSearchIterator(
      request, config);
    i.initialize(connection);
    return i;
  }
}
