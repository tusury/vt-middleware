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

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.shared.ldap.model.cursor.SearchCursor;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapOperationException;
import org.apache.directory.shared.ldap.model.message.AliasDerefMode;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.model.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;
import org.apache.directory.shared.ldap.model.message.SearchScope;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.ldaptive.DerefAliases;
import org.ldaptive.LdapEntry;
import org.ldaptive.ReferralBehavior;
import org.ldaptive.ResultCode;
import org.ldaptive.provider.ControlProcessor;
import org.ldaptive.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search iterator for apache ldap search results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapSearchIterator implements SearchIterator
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request. */
  private org.ldaptive.SearchRequest request;

  /** Control processor. */
  private ControlProcessor<Control> controlProcessor;

  /** Response data. */
  private org.ldaptive.Response<Void> response;

  /** Ldap connection. */
  private LdapConnection connection;

  /** Ldap search cursor. */
  private SearchCursor cursor;

  /** Codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;


  /**
   * Creates a new apache ldap search iterator.
   *
   * @param  sr  search request
   * @param  processor  control processor
   */
  public ApacheLdapSearchIterator(
    final org.ldaptive.SearchRequest sr,
    final ControlProcessor<Control> processor)
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
   * Initializes this apache ldap search iterator.
   *
   * @param  conn  to search with
   *
   * @throws  org.ldaptive.LdapException  if an error occurs
   */
  public void initialize(final LdapConnection conn)
    throws org.ldaptive.LdapException
  {
    connection = conn;

    boolean closeCursor = false;
    try {
      cursor = search(connection, request);
    } catch (LdapOperationException e) {
      closeCursor = true;
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (Exception e) {
      closeCursor = true;
      throw new org.ldaptive.LdapException(e);
    } finally {
      if (closeCursor) {
        try {
          if (cursor != null) {
            cursor.close();
          }
        } catch (Exception e) {
          logger.debug("Error closing search cursor", e);
        }
      }
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
   * @throws  LdapException  if an error occurs
   */
  protected SearchCursor search(
    final LdapConnection conn,
    final org.ldaptive.SearchRequest sr)
    throws LdapException
  {
    final SearchRequest apacheSr = getSearchRequest(sr);
    final Control[] c = controlProcessor.processRequestControls(
      sr.getControls());
    if (c != null) {
      apacheSr.addAllControls(c);
    }
    return connection.search(apacheSr);
  }


  /**
   * Returns an apache ldap search request object configured with the supplied
   * search request.
   *
   * @param  sr  search request containing configuration to create apache ldap
   * search request
   *
   * @return  search request
   *
   * @throws  org.apache.directory.shared.ldap.model.exception.LdapException  if
   * the search request cannot be initialized
   */
  protected SearchRequest getSearchRequest(
    final org.ldaptive.SearchRequest sr)
    throws org.apache.directory.shared.ldap.model.exception.LdapException
  {
    final SearchRequest apacheSr = new SearchRequestImpl();
    if (sr.getReturnAttributes() != null) {
      if (sr.getReturnAttributes().length == 0) {
        apacheSr.addAttributes("1.1");
      } else {
        apacheSr.addAttributes(sr.getReturnAttributes());
      }
    }
    apacheSr.setBase(new Dn(sr.getBaseDn()));

    final AliasDerefMode deref = getAliasDerefMode(sr.getDerefAliases());
    if (deref != null) {
      apacheSr.setDerefAliases(deref);
    }
    if (sr.getSearchFilter() != null) {
      apacheSr.setFilter(sr.getSearchFilter().format());
    }

    final SearchScope searchScope = getSearchScope(sr.getSearchScope());
    if (searchScope != null) {
      apacheSr.setScope(searchScope);
    }
    apacheSr.setSizeLimit(sr.getSizeLimit());
    apacheSr.setTimeLimit(Long.valueOf(sr.getTimeLimit()).intValue());
    apacheSr.setTypesOnly(sr.getTypesOnly());
    return apacheSr;
  }


  /**
   * Returns the apache ldap search scope for the supplied search scope.
   *
   * @param  ss  search scope
   *
   * @return  apache ldap search scope
   */
  protected static SearchScope getSearchScope(
    final org.ldaptive.SearchScope ss)
  {
    SearchScope scope = null;
    if (ss == org.ldaptive.SearchScope.OBJECT) {
      scope = SearchScope.OBJECT;
    } else if (ss == org.ldaptive.SearchScope.ONELEVEL) {
      scope = SearchScope.ONELEVEL;
    } else if (ss == org.ldaptive.SearchScope.SUBTREE) {
      scope = SearchScope.SUBTREE;
    }
    return scope;
  }


  /**
   * Returns the apache ldap alias deref mode for the supplied deref aliases.
   *
   * @param  deref  deref aliases
   *
   * @return  apache ldap alias deref mode
   */
  protected static AliasDerefMode getAliasDerefMode(final DerefAliases deref)
  {
    AliasDerefMode mode = null;
    if (deref == DerefAliases.ALWAYS) {
      mode = AliasDerefMode.DEREF_ALWAYS;
    } else if (deref == DerefAliases.FINDING) {
      mode = AliasDerefMode.DEREF_FINDING_BASE_OBJ;
    } else if (deref == DerefAliases.NEVER) {
      mode = AliasDerefMode.NEVER_DEREF_ALIASES;
    } else if (deref == DerefAliases.SEARCHING) {
      mode = AliasDerefMode.DEREF_IN_SEARCHING;
    }
    return mode;
  }


  /** {@inheritDoc} */
  @Override
  public boolean hasNext()
    throws org.ldaptive.LdapException
  {
    if (cursor == null || response != null) {
      return false;
    }

    boolean more = false;
    try {
      more = cursor.next();
      if (!more) {
        final SearchResultDone done = cursor.getSearchResultDone();
        final org.ldaptive.control.ResponseControl[] respControls =
          ApacheLdapUtil.processResponseControls(
            controlProcessor,
            request.getControls(),
            done);
        final boolean searchAgain = ControlProcessor.searchAgain(respControls);
        if (searchAgain) {
          cursor = search(connection, request);
          more = cursor.next();
        }
        if (!more) {
          ApacheLdapUtil.throwOperationException(
            operationRetryResultCodes,
            done.getLdapResult().getResultCode());
          response = new org.ldaptive.Response<Void>(
            null,
            ResultCode.valueOf(
              done.getLdapResult().getResultCode().getResultCode()),
            respControls);
        }
      }
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.ldaptive.LdapException e) {
      throw e;
    } catch (Exception e) {
      throw new org.ldaptive.LdapException(e);
    }
    return more;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry next()
    throws org.ldaptive.LdapException
  {
    final ApacheLdapUtil bu = new ApacheLdapUtil(request.getSortBehavior());
    bu.setBinaryAttributes(request.getBinaryAttributes());

    LdapEntry le = null;
    try {
      if (cursor.isEntry()) {
        le = bu.toLdapEntry(cursor.getEntry());
      } else if (cursor.isReferral()) {
        if (request.getReferralBehavior() == ReferralBehavior.FOLLOW) {
          throw new UnsupportedOperationException(
            "Referral following not supported");
        } else if (request.getReferralBehavior() == ReferralBehavior.IGNORE) {
          cursor.getReferral();
        } else {
          throw new org.ldaptive.LdapException(
            "Encountered referral: " + cursor.getReferral(),
            ResultCode.REFERRAL);
        }
      } else if (cursor.isIntermediate()) {
        throw new UnsupportedOperationException("Intermediate response");
      }
    } catch (LdapOperationException e) {
      ApacheLdapUtil.throwOperationException(operationRetryResultCodes, e);
    } catch (org.ldaptive.LdapException e) {
      throw e;
    } catch (Exception e) {
      throw new org.ldaptive.LdapException(e);
    }
    return le;
  }


  /** {@inheritDoc} */
  @Override
  public org.ldaptive.Response<Void> getResponse()
  {
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws org.ldaptive.LdapException {}
}
