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
package org.ldaptive.provider.opends;

import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.SearchIterator;
import org.ldaptive.sasl.QualityOfProtection;
import org.ldaptive.sasl.SaslConfig;
import org.opends.sdk.Connection;
import org.opends.sdk.ErrorResultException;
import org.opends.sdk.Modification;
import org.opends.sdk.controls.Control;
import org.opends.sdk.requests.GSSAPISASLBindRequest;
import org.opends.sdk.requests.Requests;
import org.opends.sdk.requests.SimpleBindRequest;
import org.opends.sdk.responses.BindResult;
import org.opends.sdk.responses.CompareResult;
import org.opends.sdk.responses.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OpenDS provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDSConnection implements org.ldaptive.provider.Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private Connection connection;

  /** Result codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** Control processor. */
  private ControlProcessor<Control> controlProcessor;


  /**
   * Creates a new opends ldap connection.
   *
   * @param  c  ldap connection
   */
  public OpenDSConnection(final Connection c)
  {
    connection = c;
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
  public Connection getLdapConnection()
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
      final SimpleBindRequest sbr = Requests.newSimpleBindRequest();
      if (request.getControls() != null) {
        for (Control c :
             controlProcessor.processRequestControls(request.getControls())) {
          sbr.addControl(c);
        }
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      OpenDSUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    } catch (InterruptedException e) {
      throw new LdapException(e);
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
      final SimpleBindRequest sbr = Requests.newSimpleBindRequest(
        request.getDn(), request.getCredential().getChars());
      if (request.getControls() != null) {
        for (Control c :
             controlProcessor.processRequestControls(request.getControls())) {
          sbr.addControl(c);
        }
      }

      final BindResult result = connection.bind(sbr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      OpenDSUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    } catch (InterruptedException e) {
      throw new LdapException(e);
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
    /*
    Response<Void> response = null;
    SASLBindRequest sbr = null;
    final ByteStringBuilder builder = new ByteStringBuilder();
    */
    final SaslConfig config = request.getSaslConfig();
    switch (config.getMechanism()) {

    case EXTERNAL:
      throw new UnsupportedOperationException("SASL External not supported");
      /*
      sbr = Requests.newExternalSASLBindRequest();
      break;
      */

    case DIGEST_MD5:
      throw new UnsupportedOperationException("DIGEST-MD5 not supported");
      /*
      builder.append(
        request.getCredential() != null ?
          request.getCredential().getBytes(): new byte[0]);
      sbr = Requests.newDigestMD5SASLBindRequest(
        request.getDn() != null ? request.getDn() : "",
        builder.toByteString());
      String digestMd5Realm = config instanceof DigestMd5Config
        ? ((DigestMd5Config) config).getRealm() : null;
      if (digestMd5Realm == null && request.getDn().contains("@")) {
        digestMd5Realm = request.getDn().substring(
          request.getDn().indexOf("@") + 1);
      }
      if (digestMd5Realm != null) {
        ((DigestMD5SASLBindRequest) sbr).setRealm(digestMd5Realm);
      }
      break;
      */

    case CRAM_MD5:
      throw new UnsupportedOperationException("CRAM-MD5 not supported");
      /*
      builder.append(
        request.getCredential() != null ?
          request.getCredential().getBytes(): new byte[0]);
      sbr = Requests.newCRAMMD5SASLBindRequest(
        request.getDn() != null ? request.getDn() : "",
        builder.toByteString());
      break;
      */

    case GSSAPI:
      throw new UnsupportedOperationException("GSSAPI not supported");
      /*
      builder.append(
        request.getCredential() != null ?
          request.getCredential().getBytes() : new byte[0]);
      sbr = Requests.newGSSAPISASLBindRequest(
        request.getDn() != null ? request.getDn() : "",
        builder.toByteString());
      ((GSSAPISASLBindRequest) sbr).setAuthorizationID(
        config.getAuthorizationId());
      final String gssApiRealm = config instanceof GssApiConfig
        ? ((GssApiConfig) config).getRealm() : null;
      if (gssApiRealm != null) {
        ((GSSAPISASLBindRequest) sbr).setRealm(gssApiRealm);
      }
      if (config.getQualityOfProtection() != null) {
        ((GSSAPISASLBindRequest) sbr).addQOP(
          getQualityOfProtection(config.getQualityOfProtection()));
      }
      break;
      */

    default:
      throw new IllegalArgumentException(
        "Unknown SASL authentication mechanism: " + config.getMechanism());
    }

    /*
    if (request.getControls() != null) {
      for (Control c :
           controlProcessor.processRequestControls(request.getControls())) {
        sbr.addControl(c);
      }
    }

    final BindResult result = connection.bind(sbr);
    response = new Response<Void>(
      null,
      ResultCode.valueOf(result.getResultCode().intValue()),
      controlProcessor.processResponseControls(
        request.getControls(),
        result.getControls().toArray(new Control[0])));
    return response;
    */
  }


  /**
   * Returns the SASL quality of protection string for the supplied enum.
   *
   * @param  qop  quality of protection enum
   *
   * @return  SASL quality of protection string
   */
  protected static String getQualityOfProtection(final QualityOfProtection qop)
  {
    String name = null;
    switch (qop) {

    case AUTH:
      name = GSSAPISASLBindRequest.QOP_AUTH;
      break;

    case AUTH_INT:
      name = GSSAPISASLBindRequest.QOP_AUTH_INT;
      break;

    case AUTH_CONF:
      name = GSSAPISASLBindRequest.QOP_AUTH_CONF;
      break;

    default:
      throw new IllegalArgumentException(
        "Unknown SASL quality of protection: " + qop);
    }
    return name;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final OpenDSUtil util = new OpenDSUtil();
      final org.opends.sdk.requests.AddRequest ar =
        Requests.newAddRequest(
          util.fromLdapEntry(
            new LdapEntry(request.getDn(), request.getLdapAttributes())));
      if (request.getControls() != null) {
        for (Control c :
             controlProcessor.processRequestControls(request.getControls())) {
          ar.addControl(c);
        }
      }

      final Result result = connection.add(ar);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      OpenDSUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    } catch (InterruptedException e) {
      throw new LdapException(e);
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
      final OpenDSUtil util = new OpenDSUtil();
      org.opends.sdk.requests.CompareRequest cr = null;
      if (request.getAttribute().isBinary()) {
        cr = Requests.newCompareRequest(
          request.getDn(),
          request.getAttribute().getName(),
          util.fromBinaryValues(request.getAttribute().getBinaryValues())[0]);
      } else {
        cr = Requests.newCompareRequest(
          request.getDn(),
          request.getAttribute().getName(),
          util.fromStringValues(request.getAttribute().getStringValues())[0]);
      }
      if (request.getControls() != null) {
        for (Control c :
             controlProcessor.processRequestControls(request.getControls())) {
          cr.addControl(c);
        }
      }

      final CompareResult result = connection.compare(cr);
      response = new Response<Boolean>(
        result.matched(),
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      OpenDSUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    } catch (InterruptedException e) {
      throw new LdapException(e);
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
      final org.opends.sdk.requests.DeleteRequest dr =
        Requests.newDeleteRequest(request.getDn());
      if (request.getControls() != null) {
        for (Control c :
             controlProcessor.processRequestControls(request.getControls())) {
          dr.addControl(c);
        }
      }

      final Result result = connection.delete(dr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      OpenDSUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    } catch (InterruptedException e) {
      throw new LdapException(e);
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
      final OpenDSUtil util = new OpenDSUtil();
      final org.opends.sdk.requests.ModifyRequest mr =
        Requests.newModifyRequest(request.getDn());
      for (Modification m :
           util.fromAttributeModification(
             request.getAttributeModifications())) {
        mr.addModification(m);
      }
      if (request.getControls() != null) {
        for (Control c :
             controlProcessor.processRequestControls(request.getControls())) {
          mr.addControl(c);
        }
      }

      final Result result = connection.modify(mr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      OpenDSUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    } catch (InterruptedException e) {
      throw new LdapException(e);
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
      final org.opends.sdk.requests.ModifyDNRequest mdr =
          Requests.newModifyDNRequest(request.getDn(), request.getNewDn());
      mdr.setDeleteOldRDN(request.getDeleteOldRDn());
      if (request.getControls() != null) {
        for (Control c :
             controlProcessor.processRequestControls(request.getControls())) {
          mdr.addControl(c);
        }
      }

      final Result result = connection.modifyDN(mdr);
      response = new Response<Void>(
        null,
        ResultCode.valueOf(result.getResultCode().intValue()),
        controlProcessor.processResponseControls(
          request.getControls(),
          result.getControls().toArray(new Control[0])));
    } catch (ErrorResultException e) {
      OpenDSUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        controlProcessor);
    } catch (InterruptedException e) {
      throw new LdapException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(
    final org.ldaptive.SearchRequest request)
    throws LdapException
  {
    final OpenDSSearchIterator i = new OpenDSSearchIterator(
      request,
      controlProcessor);
    i.setOperationRetryResultCodes(operationRetryResultCodes);
    i.setSearchIgnoreResultCodes(searchIgnoreResultCodes);
    i.initialize(connection);
    return i;
  }
}
