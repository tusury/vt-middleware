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
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DIGESTMD5BindRequest;
import com.unboundid.ldap.sdk.DN;
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
import org.ldaptive.provider.ControlProcessor;
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
public class UnboundIdConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Result codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** Control processor. */
  private ControlProcessor<Control> controlProcessor;


  /**
   * Creates a new unboundid ldap connection.
   *
   * @param  lc  ldap connection
   */
  public UnboundIdConnection(final LDAPConnection lc)
  {
    connection = lc;
  }


  /**
   * Returns the result codes to retry operations on.
   *
   * @return  result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the result codes to retry operations on.
   *
   * @param  codes  result codes
   */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    operationRetryResultCodes = codes;
  }


  /**
   * Returns the search ignore result codes.
   *
   * @return  result codes to ignore
   */
  public ResultCode[] getSearchIgnoreResultCodes()
  {
    return searchIgnoreResultCodes;
  }


  /**
   * Sets the search ignore result codes.
   *
   * @param  codes  to ignore
   */
  public void setSearchIgnoreResultCodes(final ResultCode[] codes)
  {
    searchIgnoreResultCodes = codes;
  }


  /**
   * Returns the control processor.
   *
   * @return  control processor
   */
  public ControlProcessor<Control> getControlProcessor()
  {
    return controlProcessor;
  }


  /**
   * Sets the control processor.
   *
   * @param  processor  control processor
   */
  public void setControlProcessor(final ControlProcessor<Control> processor)
  {
    controlProcessor = processor;
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
    Response<Void> response = null;
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
      SimpleBindRequest sbr = null;
      if (request.getControls() != null) {
        sbr = new SimpleBindRequest(
          "",
          new byte[0],
          controlProcessor.processRequestControls(request.getControls()));
      } else {
        sbr = new SimpleBindRequest();
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
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
      SimpleBindRequest sbr = null;
      if (request.getControls() != null) {
        sbr = new SimpleBindRequest(
          new DN(request.getDn()),
          request.getCredential().getBytes(),
          controlProcessor.processRequestControls(request.getControls()));
      } else {
        sbr = new SimpleBindRequest(
          request.getDn(),
          request.getCredential().getBytes());
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
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
      SASLBindRequest sbr = null;
      final SaslConfig config = request.getSaslConfig();
      switch (config.getMechanism()) {

      case EXTERNAL:
        throw new UnsupportedOperationException("SASL External not supported");
        /* current implementation appears to be broken
         * sbr = new EXTERNALBindRequest(
         * controlHandler.processRequestControls(request.getControls()));
         * break;
         */

      case DIGEST_MD5:

        String realm = config instanceof DigestMd5Config
          ? ((DigestMd5Config) config).getRealm() : null;
        if (realm == null && request.getDn().contains("@")) {
          realm = request.getDn().substring(request.getDn().indexOf("@") + 1);
        }
        sbr = new DIGESTMD5BindRequest(
          request.getDn(),
          config.getAuthorizationId(),
          request.getCredential() != null ? request.getCredential().getBytes()
                                          : null,
          realm,
          controlProcessor.processRequestControls(request.getControls()));
        break;

      case CRAM_MD5:
        sbr = new CRAMMD5BindRequest(
          request.getDn(),
          request.getCredential() != null ? request.getCredential().getBytes()
                                          : null,
          controlProcessor.processRequestControls(request.getControls()));
        break;

      case GSSAPI:

        final GSSAPIBindRequestProperties props =
          new GSSAPIBindRequestProperties(
            request.getDn(),
            request.getCredential() != null ? request.getCredential()
              .getBytes() : null);
        props.setAuthorizationID(config.getAuthorizationId());
        props.setRealm(
          config instanceof GssApiConfig ? ((GssApiConfig) config).getRealm()
                                         : null);
        sbr = new GSSAPIBindRequest(
          props,
          controlProcessor.processRequestControls(request.getControls()));
        break;

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " + config.getMechanism());
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
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
      final UnboundIdUtil util = new UnboundIdUtil();
      final com.unboundid.ldap.sdk.AddRequest ar =
        new com.unboundid.ldap.sdk.AddRequest(
          new DN(request.getDn()),
          util.fromLdapAttributes(request.getLdapAttributes()),
          controlProcessor.processRequestControls(request.getControls()));

      final LDAPResult result = connection.add(ar);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
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
      com.unboundid.ldap.sdk.CompareRequest cr = null;
      if (request.getAttribute().isBinary()) {
        cr = new com.unboundid.ldap.sdk.CompareRequest(
          new DN(request.getDn()),
          request.getAttribute().getName(),
          request.getAttribute().getBinaryValue(),
          controlProcessor.processRequestControls(request.getControls()));
      } else {
        cr = new com.unboundid.ldap.sdk.CompareRequest(
          new DN(request.getDn()),
          request.getAttribute().getName(),
          request.getAttribute().getStringValue(),
          controlProcessor.processRequestControls(request.getControls()));
      }

      final CompareResult result = connection.compare(cr);
      response = new Response<Boolean>(
        result.compareMatched(),
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
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
          controlProcessor.processRequestControls(request.getControls()));

      final LDAPResult result = connection.delete(dr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
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
      final UnboundIdUtil bu = new UnboundIdUtil();
      final com.unboundid.ldap.sdk.ModifyRequest mr =
        new com.unboundid.ldap.sdk.ModifyRequest(
          new DN(request.getDn()),
          bu.fromAttributeModification(request.getAttributeModifications()),
          controlProcessor.processRequestControls(request.getControls()));

      final LDAPResult result = connection.modify(mr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
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
          controlProcessor.processRequestControls(request.getControls()));

      final LDAPResult result = connection.modifyDN(mdr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getResponseControls()));
    } catch (LDAPException e) {
      UnboundIdUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final UnboundIdSearchIterator i = new UnboundIdSearchIterator(
      request,
      controlProcessor);
    i.setOperationRetryResultCodes(operationRetryResultCodes);
    i.setSearchIgnoreResultCodes(searchIgnoreResultCodes);
    i.initialize(connection);
    return i;
  }
}
