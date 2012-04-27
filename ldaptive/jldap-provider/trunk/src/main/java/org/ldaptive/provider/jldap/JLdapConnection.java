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
import java.util.Arrays;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPReferralException;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.LDAPResponseQueue;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;
import com.novell.security.sasl.RealmCallback;
import com.novell.security.sasl.RealmChoiceCallback;
import org.ldaptive.AddRequest;
import org.ldaptive.BindRequest;
import org.ldaptive.CompareRequest;
import org.ldaptive.DeleteRequest;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.LdapUtils;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Request;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.Connection;
import org.ldaptive.provider.ControlProcessor;
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
      response = createResponse(request, null, lr);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      response = createResponse(request, null, lr);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      processLDAPException(e);
    }
    return new Response<Void>(null, ResultCode.SUCCESS);
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
      response = createResponse(request, null, lr);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      response = createResponse(
        request, lr.getResultCode() == ResultCode.COMPARE_TRUE.value(), lr);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      response = createResponse(request, null, lr);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      response = createResponse(request, null, lr);
    } catch (LDAPException e) {
      processLDAPException(e);
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
      response = createResponse(request, null, lr);
    } catch (LDAPException e) {
      processLDAPException(e);
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(final SearchRequest request)
    throws LdapException
  {
    final JLdapSearchIterator i = new JLdapSearchIterator(request);
    i.initialize();
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
    LDAPConstraints constraints = connection.getConstraints();
    if (constraints == null) {
      constraints = new LDAPConstraints();
    }
    if (request.getControls() != null) {
      constraints.setControls(
        config.getControlProcessor().processRequestControls(
          request.getControls()));
    }
    constraints.setReferralFollowing(request.getFollowReferrals());
    return constraints;
  }


  /**
   * Creates an operation response with the supplied response data.
   *
   * @param  <T>  type of response
   * @param  request  containing controls
   * @param  result  of the operation
   * @param  ldapResponse  provider response
   *
   * @return  operation response
   */
  protected <T> Response<T> createResponse(
    final Request request,
    final T result,
    final LDAPResponse ldapResponse)
  {
    return new Response<T>(
      result,
      ResultCode.valueOf(ldapResponse.getResultCode()),
      ldapResponse.getErrorMessage(),
      ldapResponse.getMatchedDN(),
      config.getControlProcessor().processResponseControls(
        request.getControls(), ldapResponse.getControls()),
      ldapResponse.getReferrals());
  }


  /**
   * Determines if the supplied ldap exception should result in an operation
   * retry.
   *
   * @param  e  that was produced
   *
   * @throws  LdapException  wrapping the ldap exception
   */
  protected void processLDAPException(final LDAPException e)
    throws LdapException
  {
    ProviderUtils.throwOperationException(
      config.getOperationRetryResultCodes(),
      e,
      e.getResultCode(),
      e.getMatchedDN(),
      null,
      null,
      true);
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


  /**
   * Search iterator for JLdap search results.
   */
  protected class JLdapSearchIterator implements SearchIterator
  {

    /** Search request. */
    private final SearchRequest request;

    /** Response data. */
    private Response<Void> response;

    /** Response result code. */
    private ResultCode responseResultCode;

    /** Referral URLs. */
    private String[] referralUrls;

    /** Ldap search results. */
    private LDAPSearchResults results;


    /**
     * Creates a new jldap search iterator.
     *
     * @param  sr  search request
     */
    public JLdapSearchIterator(final SearchRequest sr)
    {
      request = sr;
    }


    /**
     * Initializes this jldap search iterator.
     *
     * @throws  LdapException  if an error occurs
     */
    public void initialize()
      throws LdapException
    {
      try {
        results = search(connection, request);
      } catch (LDAPException e) {
        processLDAPException(e);
      }
    }


    /**
     * Executes an ldap search.
     *
     * @param  conn  to search with
     * @param  sr  to read properties from
     *
     * @return  ldap search results
     *
     * @throws  LDAPException  if an error occurs
     */
    protected LDAPSearchResults search(
      final LDAPConnection conn,
      final SearchRequest sr)
      throws LDAPException
    {
      final LDAPSearchConstraints constraints = getLDAPSearchConstraints(sr);
      final LDAPControl[] lc =
        config.getControlProcessor().processRequestControls(sr.getControls());
      if (lc != null) {
        constraints.setControls(lc);
      }
      return
        conn.search(
          sr.getBaseDn(),
          getSearchScope(sr.getSearchScope()),
          sr.getSearchFilter() != null ? sr.getSearchFilter().format() : null,
          getReturnAttributes(sr),
          sr.getTypesOnly(),
          constraints);
    }


    /**
     * Returns the jldap integer constant for the supplied search scope.
     *
     * @param  ss  search scope
     *
     * @return  integer constant
     */
    protected int getSearchScope(final SearchScope ss)
    {
      int scope = -1;
      if (ss == SearchScope.OBJECT) {
        scope = LDAPConnection.SCOPE_BASE;
      } else if (ss == SearchScope.ONELEVEL) {
        scope = LDAPConnection.SCOPE_ONE;
      } else if (ss == SearchScope.SUBTREE) {
        scope = LDAPConnection.SCOPE_SUB;
      }
      return scope;
    }


    /**
     * Returns an array of attribute names expected from the search request.
     * Uses the '1.1' special attribute name if no attributes are requested.
     *
     * @param  sr  containing return attributes
     *
     * @return  attribute names for JLDAP
     */
    protected String[] getReturnAttributes(final SearchRequest sr)
    {
      String[] returnAttrs = null;
      if (sr.getReturnAttributes() != null) {
        if (sr.getReturnAttributes().length == 0) {
          returnAttrs = new String[] {"1.1"};
        } else {
          returnAttrs = sr.getReturnAttributes();
        }
      }
      return returnAttrs;
    }


    /**
     * Returns an ldap search constraints object configured with the supplied
     * search request.
     *
     * @param  sr  search request containing configuration to create search
     * constraints
     *
     * @return  ldap search constraints
     */
    protected LDAPSearchConstraints getLDAPSearchConstraints(
      final SearchRequest sr)
    {
      LDAPSearchConstraints constraints = connection.getSearchConstraints();
      if (constraints == null) {
        constraints = new LDAPSearchConstraints();
      }
      constraints.setServerTimeLimit(
        Long.valueOf(sr.getTimeLimit()).intValue());
      constraints.setMaxResults(Long.valueOf(sr.getSizeLimit()).intValue());
      if (sr.getDerefAliases() != null) {
        if (sr.getDerefAliases() == DerefAliases.ALWAYS) {
          constraints.setDereference(LDAPSearchConstraints.DEREF_ALWAYS);
        } else if (sr.getDerefAliases() == DerefAliases.FINDING) {
          constraints.setDereference(LDAPSearchConstraints.DEREF_FINDING);
        } else if (sr.getDerefAliases() == DerefAliases.NEVER) {
          constraints.setDereference(LDAPSearchConstraints.DEREF_NEVER);
        } else if (sr.getDerefAliases() == DerefAliases.SEARCHING) {
          constraints.setDereference(LDAPSearchConstraints.DEREF_SEARCHING);
        }
      }
      constraints.setReferralFollowing(sr.getFollowReferrals());
      return constraints;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNext()
      throws LdapException
    {
      if (results == null || response != null) {
        return false;
      }

      boolean more = false;
      try {
        more = results.hasMore();
        if (!more) {
          final ResponseControl[] respControls =
            config.getControlProcessor().processResponseControls(
              request.getControls(),
              results.getResponseControls());
          final boolean searchAgain = ControlProcessor.searchAgain(
            respControls);
          if (searchAgain) {
            results = search(connection, request);
            more = results.hasMore();
          }
          if (!more) {
            response = new Response<Void>(
              null,
              responseResultCode != null ? responseResultCode
                : ResultCode.SUCCESS,
              null,
              null,
              respControls,
              referralUrls);
          }
        }
      } catch (LDAPReferralException e) {
        referralUrls = LdapUtils.concatArrays(e.getReferrals(), referralUrls);
        responseResultCode = ResultCode.valueOf(e.getResultCode());
      } catch (LDAPException e) {
        final ResultCode rc = ignoreSearchException(
          config.getSearchIgnoreResultCodes(), e);
        if (rc == null) {
          processLDAPException(e);
        }
        response = new Response<Void>(
          null,
          rc,
          e.getLDAPErrorMessage(),
          null,
          config.getControlProcessor().processResponseControls(
            request.getControls(), results.getResponseControls()),
          referralUrls);
      }
      return more;
    }


    /** {@inheritDoc} */
    @Override
    public LdapEntry next()
      throws LdapException
    {
      final JLdapUtils bu = new JLdapUtils(request.getSortBehavior());
      bu.setBinaryAttributes(request.getBinaryAttributes());

      LdapEntry le = null;
      try {
        final LDAPEntry entry = results.next();
        logger.trace("reading search entry: {}", entry);
        le = bu.toLdapEntry(entry);
      } catch (LDAPReferralException e) {
        logger.trace(
          "reading search reference: {}", Arrays.toString(e.getReferrals()));
        referralUrls = LdapUtils.concatArrays(e.getReferrals(), referralUrls);
        responseResultCode = ResultCode.valueOf(e.getResultCode());
      } catch (LDAPException e) {
        final ResultCode rc = ignoreSearchException(
          config.getSearchIgnoreResultCodes(), e);
        if (rc == null) {
          processLDAPException(e);
        }
        responseResultCode = rc;
      }
      return le;
    }


    /**
     * Determines whether the supplied ldap exception should be ignored.
     *
     * @param  ignoreResultCodes  to match against the exception
     * @param  e  ldap exception to match
     *
     * @return  result code that should be ignored or null
     */
    protected ResultCode ignoreSearchException(
      final ResultCode[] ignoreResultCodes,
      final LDAPException e)
    {
      ResultCode ignore = null;
      if (ignoreResultCodes != null && ignoreResultCodes.length > 0) {
        for (ResultCode rc : ignoreResultCodes) {
          if (e.getResultCode() == rc.value()) {
            logger.debug("Ignoring ldap exception", e);
            ignore = rc;
            break;
          }
        }
      }
      return ignore;
    }


    /** {@inheritDoc} */
    @Override
    public Response<Void> getResponse()
    {
      return response;
    }


    /** {@inheritDoc} */
    @Override
    public void close()
      throws LdapException {}
  }
}
