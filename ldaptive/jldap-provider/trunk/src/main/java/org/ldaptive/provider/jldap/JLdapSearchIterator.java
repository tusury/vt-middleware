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

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReferralBehavior;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search iterator for JLdap search results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JLdapSearchIterator implements SearchIterator
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request. */
  private SearchRequest request;

  /** Control processor. */
  private ControlProcessor<LDAPControl> controlProcessor;

  /** Response data. */
  private Response<Void> response;

  /** Response result code. */
  private ResultCode responseResultCode;

  /** Ldap connection. */
  private LDAPConnection connection;

  /** Ldap search results. */
  private LDAPSearchResults results;

  /** Codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;


  /**
   * Creates a new jldap search iterator.
   *
   * @param  sr  search request
   * @param  processor  control processor
   */
  public JLdapSearchIterator(
    final SearchRequest sr,
    final ControlProcessor<LDAPControl> processor)
  {
    request = sr;
    controlProcessor = processor;
  }


  /**
   * Returns the ldap result codes to retry operations on.
   *
   * @return  result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the ldap result codes to retry operations on.
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
   * Initializes this jldap search iterator.
   *
   * @param  conn  to search with
   *
   * @throws  LdapException  if an error occurs
   */
  public void initialize(final LDAPConnection conn)
    throws LdapException
  {
    connection = conn;
    try {
      results = search(connection, request);
    } catch (LDAPException e) {
      JLdapUtil.throwOperationException(operationRetryResultCodes, e);
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
    final LDAPSearchConstraints constraints = getLDAPSearchConstraints(request);
    final LDAPControl[] lc = controlProcessor.processRequestControls(
      sr.getControls());
    if (lc != null) {
      constraints.setControls(lc);
    }
    return
      conn.search(
        request.getBaseDn(),
        getSearchScope(request.getSearchScope()),
        request.getSearchFilter() != null ?
          request.getSearchFilter().format() : null,
        getReturnAttributes(request),
        request.getTypesOnly(),
        constraints);
  }


  /**
   * Returns the jldap integer constant for the supplied search scope.
   *
   * @param  ss  search scope
   *
   * @return  integer constant
   */
  protected static int getSearchScope(final SearchScope ss)
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
   * Returns an array of attribute names expected from the search request. Uses
   * the '1.1' special attribute name if no attributes are requested.
   *
   * @param  sr  containing return attributes
   *
   * @return  attribute names for JLDAP
   */
  protected static String[] getReturnAttributes(final SearchRequest sr)
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
    final LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setServerTimeLimit(Long.valueOf(sr.getTimeLimit()).intValue());
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
    if (sr.getReferralBehavior() != null) {
      if (sr.getReferralBehavior() == ReferralBehavior.FOLLOW) {
        constraints.setReferralFollowing(true);
      } else if (sr.getReferralBehavior() == ReferralBehavior.THROW) {
        constraints.setReferralFollowing(false);
      }
    }
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
          controlProcessor.processResponseControls(
            request.getControls(),
            results.getResponseControls());
        final boolean searchAgain = ControlProcessor.searchAgain(respControls);
        if (searchAgain) {
          results = search(connection, request);
          more = results.hasMore();
        }
        if (!more) {
          response = new Response<Void>(
            null,
            responseResultCode != null ? responseResultCode
                                       : ResultCode.SUCCESS,
            respControls);
        }
      }
    } catch (LDAPException e) {
      final ResultCode rc = ignoreSearchException(searchIgnoreResultCodes, e);
      if (rc == null) {
        JLdapUtil.throwOperationException(operationRetryResultCodes, e);
      }
      response = new Response<Void>(
        null,
        rc,
        controlProcessor.processResponseControls(
          request.getControls(),
          results.getResponseControls()));
    }
    return more;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry next()
    throws LdapException
  {
    final JLdapUtil bu = new JLdapUtil(request.getSortBehavior());
    bu.setBinaryAttributes(request.getBinaryAttributes());

    LdapEntry le = null;
    try {
      final LDAPEntry entry = results.next();
      le = bu.toLdapEntry(entry);
    } catch (LDAPException e) {
      final ResultCode rc = ignoreSearchException(searchIgnoreResultCodes, e);
      if (rc == null) {
        JLdapUtil.throwOperationException(operationRetryResultCodes, e);
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
    if (
      ignore == null &&
        e.getResultCode() == ResultCode.REFERRAL.value() &&
        request.getReferralBehavior() == ReferralBehavior.IGNORE) {
      ignore = ResultCode.SUCCESS;
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
