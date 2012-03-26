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

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPControl;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPModification;
import netscape.ldap.LDAPModificationSet;
import netscape.ldap.LDAPReferralException;
import netscape.ldap.LDAPSortKey;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.OperationException;
import org.ldaptive.ResultCode;
import org.ldaptive.SortBehavior;
import org.ldaptive.control.SortKey;
import org.ldaptive.provider.ControlProcessor;

/**
 * Provides methods for converting between Netscape specific objects and
 * ldaptive specific objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeUtil
{

  /** Default binary attributes. */
  protected static final String[] DEFAULT_BINARY_ATTRS = new String[] {
    "userPassword",
    "jpegPhoto",
    "userCertificate",
  };

  /** Ldap result sort behavior. */
  private final SortBehavior sortBehavior;

  /** Attributes that should be treated as binary. */
  private List<String> binaryAttrs = Arrays.asList(DEFAULT_BINARY_ATTRS);


  /** Default constructor. */
  public NetscapeUtil()
  {
    sortBehavior = SortBehavior.getDefaultSortBehavior();
  }


  /**
   * Creates a new netscape util.
   *
   * @param  sb  sort behavior
   */
  public NetscapeUtil(final SortBehavior sb)
  {
    sortBehavior = sb;
  }


  /**
   * Returns the list of binary attributes.
   *
   * @return  list of binary attributes
   */
  public List<String> getBinaryAttributes()
  {
    return binaryAttrs;
  }


  /**
   * Sets the list of binary attributes.
   *
   * @param  s  binary attributes
   */
  public void setBinaryAttributes(final String[] s)
  {
    if (s != null) {
      binaryAttrs = Arrays.asList(s);
    }
  }


  /**
   * Returns a netscape attribute that represents the values in the supplied
   * ldap attribute.
   *
   * @param  la  ldap attribute
   *
   * @return  netscape attribute
   */
  public LDAPAttribute fromLdapAttribute(final LdapAttribute la)
  {
    final LDAPAttribute attribute = new LDAPAttribute(la.getName());
    if (la.isBinary()) {
      for (byte[] b : la.getBinaryValues()) {
        attribute.addValue(b);
      }
    } else {
      for (String s : la.getStringValues()) {
        attribute.addValue(s);
      }
    }
    return attribute;
  }


  /**
   * Returns an ldap attribute using the supplied netscape attribute.
   *
   * @param  a  netscape attribute
   *
   * @return  ldap attribute
   */
  public LdapAttribute toLdapAttribute(final LDAPAttribute a)
  {
    boolean isBinary = false;
    if (a.getName().indexOf(";binary") != -1) {
      isBinary = true;
    } else if (binaryAttrs != null && binaryAttrs.contains(a.getName())) {
      isBinary = true;
    }

    final LdapAttribute la = new LdapAttribute(sortBehavior, isBinary);
    la.setName(a.getName());
    if (isBinary) {
      la.addBinaryValue(a.getByteValueArray());
    } else {
      la.addStringValue(a.getStringValueArray());
    }
    return la;
  }


  /**
   * Returns a list of netscape attribute that represents the values in the
   * supplied ldap attributes.
   *
   * @param  c  ldap attributes
   *
   * @return  netscape attributes
   */
  public LDAPAttributeSet fromLdapAttributes(final Collection<LdapAttribute> c)
  {
    final LDAPAttributeSet attributes = new LDAPAttributeSet();
    for (LdapAttribute a : c) {
      attributes.add(fromLdapAttribute(a));
    }
    return attributes;
  }


  /**
   * Returns an ldap entry using the supplied netscape entry.
   *
   * @param  e  netscape entry
   *
   * @return  ldap entry
   */
  @SuppressWarnings("unchecked")
  public LdapEntry toLdapEntry(final LDAPEntry e)
  {
    final LdapEntry le = new LdapEntry(sortBehavior);
    le.setDn(e.getDN() != null ? e.getDN() : "");

    final Enumeration<LDAPAttribute> en = e.getAttributeSet().getAttributes();
    while (en.hasMoreElements()) {
      le.addAttribute(toLdapAttribute(en.nextElement()));
    }
    return le;
  }


  /**
   * Returns a netscape ldap entry that represents the supplied ldap entry.
   *
   * @param  le  ldap entry
   *
   * @return  netscape ldap entry
   */
  public LDAPEntry fromLdapEntry(final LdapEntry le)
  {
    return new LDAPEntry(le.getDn(), fromLdapAttributes(le.getAttributes()));
  }


  /**
   * Returns netscape modifications using the supplied attribute modifications.
   *
   * @param  am  attribute modifications
   *
   * @return  netscape modifications
   */
  public LDAPModificationSet fromAttributeModification(
    final AttributeModification[] am)
  {
    final LDAPModificationSet mods = new LDAPModificationSet();
    for (int i = 0; i < am.length; i++) {
      mods.add(
        getModificationType(am[i].getAttributeModificationType()),
        fromLdapAttribute(am[i].getAttribute()));
    }
    return mods;
  }


  /**
   * Determines whether to throw operation exception or ldap exception. If
   * operation exception is thrown, the operation will be retried. Otherwise the
   * exception is propagated out.
   *
   * @param  operationRetryResultCodes  to compare result code against
   * @param  e  netscape exception to examine
   * @param  processor  control processor
   *
   * @throws  OperationException  if the operation should be retried
   * @throws  LdapException  to propagate the exception out
   */
  public static void throwOperationException(
    final org.ldaptive.ResultCode[] operationRetryResultCodes,
    final LDAPException e,
    final ControlProcessor<LDAPControl> processor)
    throws LdapException
  {
    int code = e.getLDAPResultCode();
    if (e instanceof LDAPReferralException) {
      code = ResultCode.REFERRAL.value();
    }
    if (
      operationRetryResultCodes != null &&
        operationRetryResultCodes.length > 0) {
      for (ResultCode rc : operationRetryResultCodes) {
        if (rc.value() == code) {
          throw new OperationException(e, ResultCode.valueOf(code));
        }
      }
    }
    throw new LdapException(e, ResultCode.valueOf(code));
  }


  /**
   * Returns netscape sort keys using the supplied sort keys.
   *
   * @param  sk  sort keys
   *
   * @return  netscape sort keys
   */
  public static LDAPSortKey[] fromSortKey(final SortKey[] sk)
  {
    LDAPSortKey[] keys = null;
    if (sk != null) {
      keys = new LDAPSortKey[sk.length];
      for (int i = 0; i < sk.length; i++) {
        keys[i] = new LDAPSortKey(
          sk[i].getAttributeDescription(),
          sk[i].getReverseOrder(),
          sk[i].getMatchingRuleId());
      }
    }
    return keys;
  }


  /**
   * Returns the netscape modification type for the supplied attribute
   * modification type.
   *
   * @param  am  attribute modification type
   *
   * @return  modification type
   */
  protected static int getModificationType(final AttributeModificationType am)
  {
    int type = -1;
    if (am == AttributeModificationType.ADD) {
      type = LDAPModification.ADD;
    } else if (am == AttributeModificationType.REMOVE) {
      type = LDAPModification.DELETE;
    } else if (am == AttributeModificationType.REPLACE) {
      type = LDAPModification.REPLACE;
    }
    return type;
  }
}
