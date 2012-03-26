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
package org.ldaptive.provider.apache;

import java.io.IOException;
import org.apache.directory.ldap.client.api.CramMd5Request;
import org.apache.directory.ldap.client.api.DigestMd5Request;
import org.apache.directory.ldap.client.api.GssApiRequest;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.exception.LdapOperationException;
import org.apache.directory.shared.ldap.model.message.AddRequestImpl;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.BindRequestImpl;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareRequestImpl;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.name.Dn;
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
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache LDAP provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection. */
  private LdapNetworkConnection connection;

  /** Provider configuration. */
  private final ApacheLdapProviderConfig config;


  /**
   * Creates a new apache ldap connection.
   *
   * @param  lc  ldap connection
   * @param  pc  provider configuration
   */
  public ApacheLdapConnection(
    final LdapNetworkConnection lc, final ApacheLdapProviderConfig pc)
  {
    connection = lc;
    config = pc;
  }


  /**
   * Returns the underlying ldap connection.
   *
   * @return  ldap connection
   */
  public LdapNetworkConnection getLdapConnection()
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
          connection.unBind();
        }
      } catch (
        org.apache.directory.shared.ldap.model.exception.LdapException e) {
        logger.error("Error unbinding from LDAP", e);
      }
      try {
        connection.close();
      } catch (IOException e) {
        throw new LdapException(e);
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
      final BindRequestImpl bri = new BindRequestImpl();
      if (request.getControls() != null) {
        bri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }

      final BindResponse br = connection.bind(bri);
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        br.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(br.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          br));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
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
      final BindRequestImpl bri = new BindRequestImpl();
      if (request.getControls() != null) {
        bri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      bri.setVersion3(true);
      bri.setSimple(true);
      bri.setDn(new Dn(request.getDn()));
      bri.setCredentials(request.getCredential().getBytes());

      final BindResponse br = connection.bind(bri);
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        br.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(br.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          br));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
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
    Response<Void> response = null;
    try {
      BindResponse br = null;
      switch (request.getSaslConfig().getMechanism()) {

      case EXTERNAL:
        throw new UnsupportedOperationException("SASL External not supported");

      case DIGEST_MD5:

        final DigestMd5Request digestMd5Request = ApacheLdapSaslUtil
          .createDigestMd5Request(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(digestMd5Request);
        break;

      case CRAM_MD5:

        final CramMd5Request cramMd5Request = ApacheLdapSaslUtil
          .createCramMd5Request(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(cramMd5Request);
        break;

      case GSSAPI:

        final GssApiRequest gssApiRequest = ApacheLdapSaslUtil
          .createGssApiRequest(
            request.getDn(),
            request.getCredential(),
            request.getSaslConfig());
        br = connection.bind(gssApiRequest);
        break;

      default:
        throw new IllegalArgumentException(
          "Unknown SASL authentication mechanism: " +
          request.getSaslConfig().getMechanism());
      }
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        br.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(br.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          br));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
      throw new LdapException(e);
    } catch (IOException e) {
      throw new LdapException(e);
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
      final ApacheLdapUtil bu = new ApacheLdapUtil();
      final AddRequestImpl ari = new AddRequestImpl();
      if (request.getControls() != null) {
        ari.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      ari.setEntry(
        bu.fromLdapEntry(
          new LdapEntry(request.getDn(), request.getLdapAttributes())));

      final AddResponse ar = connection.add(ari);
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        ar.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(ar.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          ar));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
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
      final CompareRequestImpl cri = new CompareRequestImpl();
      if (request.getControls() != null) {
        cri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      cri.setName(new Dn(request.getDn()));
      cri.setAttributeId(request.getAttribute().getName());
      if (request.getAttribute().isBinary()) {
        cri.setAssertionValue(request.getAttribute().getBinaryValue());
      } else {
        cri.setAssertionValue(request.getAttribute().getStringValue());
      }

      final CompareResponse cr = connection.compare(cri);
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        cr.getLdapResult().getResultCode());
      response = new Response<Boolean>(
        cr.isTrue(),
        ResultCode.valueOf(cr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          cr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
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
      final DeleteRequestImpl dri = new DeleteRequestImpl();
      if (request.getControls() != null) {
        dri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      dri.setName(new Dn(request.getDn()));

      final DeleteResponse dr = connection.delete(dri);
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        dr.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(dr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          dr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
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
      final ApacheLdapUtil bu = new ApacheLdapUtil();
      final ModifyRequestImpl mri = new ModifyRequestImpl();
      if (request.getControls() != null) {
        mri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      mri.setName(new Dn(request.getDn()));
      for (
        Modification m :
        bu.fromAttributeModification(request.getAttributeModifications())) {
        mri.addModification(m);
      }

      final ModifyResponse mr = connection.modify(mri);
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        mr.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(mr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          mr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
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
      final Dn dn = new Dn(request.getDn());
      final Dn newDn = new Dn(request.getNewDn());
      final ModifyDnRequestImpl mdri = new ModifyDnRequestImpl();
      if (request.getControls() != null) {
        mdri.addAllControls(
          config.getControlProcessor().processRequestControls(
            request.getControls()));
      }
      mdri.setName(dn);
      mdri.setNewRdn(newDn.getRdn());
      mdri.setNewSuperior(newDn.getParent());
      mdri.setDeleteOldRdn(request.getDeleteOldRDn());

      final ModifyDnResponse mdr = connection.modifyDn(mdri);
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(),
        mdr.getLdapResult().getResultCode());
      response = new Response<Void>(
        null,
        ResultCode.valueOf(mdr.getLdapResult().getResultCode().getResultCode()),
        ApacheLdapUtil.processResponseControls(
          config.getControlProcessor(),
          request.getControls(),
          mdr));
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(
        config.getOperationRetryResultCodes(), e);
    } catch (org.apache.directory.shared.ldap.model.exception.LdapException e) {
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
    final ApacheLdapSearchIterator i = new ApacheLdapSearchIterator(
      request, config);
    i.initialize(connection);
    return i;
  }
}
