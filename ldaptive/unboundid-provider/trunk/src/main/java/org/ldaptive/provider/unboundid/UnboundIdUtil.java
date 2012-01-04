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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.controls.SortKey;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.OperationException;
import org.ldaptive.SortBehavior;
import org.ldaptive.provider.ControlProcessor;

/**
 * Provides methods for converting between Unbound ID specific objects and
 * ldaptive specific objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnboundIdUtil
{

  /** Ldap result sort behavior. */
  private SortBehavior sortBehavior;

  /** Attributes that should be treated as binary. */
  private List<String> binaryAttrs;


  /** Default constructor. */
  public UnboundIdUtil()
  {
    sortBehavior = SortBehavior.getDefaultSortBehavior();
  }


  /**
   * Creates a new unboundid util.
   *
   * @param  sb  sort behavior
   */
  public UnboundIdUtil(final SortBehavior sb)
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
   * Returns an unbound id attribute that represents the values in the supplied
   * ldap attribute.
   *
   * @param  la  ldap attribute
   *
   * @return  unbound id attribute
   */
  public Attribute fromLdapAttribute(final LdapAttribute la)
  {
    Attribute attribute = null;
    if (la.isBinary()) {
      attribute = new Attribute(
        la.getName(),
        la.getBinaryValues().toArray(new byte[la.size()][]));
    } else {
      attribute = new Attribute(la.getName(), la.getStringValues());
    }
    return attribute;
  }


  /**
   * Returns an ldap attribute using the supplied unbound id attribute.
   *
   * @param  a  unbound id attribute
   *
   * @return  ldap attribute
   */
  public LdapAttribute toLdapAttribute(final Attribute a)
  {
    final LdapAttribute la = new LdapAttribute(
      sortBehavior,
      a.needsBase64Encoding());
    la.setName(a.getName());
    if (a.needsBase64Encoding()) {
      la.addBinaryValue(a.getValueByteArrays());
    } else {
      la.addStringValue(a.getValues());
    }
    return la;
  }


  /**
   * Returns a list of unbound id attribute that represents the values in the
   * supplied ldap attributes.
   *
   * @param  c  ldap attributes
   *
   * @return  unbound id attributes
   */
  public Attribute[] fromLdapAttributes(final Collection<LdapAttribute> c)
  {
    final List<Attribute> attributes = new ArrayList<Attribute>();
    for (LdapAttribute a : c) {
      attributes.add(fromLdapAttribute(a));
    }
    return attributes.toArray(new Attribute[attributes.size()]);
  }


  /**
   * Returns an ldap entry using the supplied unbound id entry.
   *
   * @param  e  unbound id entry
   *
   * @return  ldap entry
   */
  public LdapEntry toLdapEntry(final Entry e)
  {
    final LdapEntry le = new LdapEntry(sortBehavior);
    le.setDn(e.getDN());
    for (Attribute a : e.getAttributes()) {
      le.addAttribute(toLdapAttribute(a));
    }
    return le;
  }


  /**
   * Returns unbound id modifications using the supplied attribute
   * modifications.
   *
   * @param  am  attribute modifications
   *
   * @return  unbound id modifications
   */
  public Modification[] fromAttributeModification(
    final AttributeModification[] am)
  {
    final Modification[] mods = new Modification[am.length];
    for (int i = 0; i < am.length; i++) {
      final Attribute a = fromLdapAttribute(am[i].getAttribute());
      if (am[i].getAttribute().isBinary()) {
        mods[i] = new Modification(
          getModificationType(am[i].getAttributeModificationType()),
          a.getName(),
          a.getValueByteArrays());
      } else {
        mods[i] = new Modification(
          getModificationType(am[i].getAttributeModificationType()),
          a.getName(),
          a.getValues());
      }
    }
    return mods;
  }


  /**
   * Determines whether to throw operation exception or do nothing. If operation
   * exception is thrown, the operation will be retried.
   *
   * @param  operationRetryResultCodes  to compare result code against
   * @param  code  unbound id result code to examine
   *
   * @throws  OperationException  if the operation should be retried
   */
  public static void throwOperationException(
    final org.ldaptive.ResultCode[] operationRetryResultCodes,
    final ResultCode code)
    throws OperationException
  {
    if (
      operationRetryResultCodes != null &&
        operationRetryResultCodes.length > 0) {
      for (org.ldaptive.ResultCode rc : operationRetryResultCodes) {
        if (rc.value() == code.intValue()) {
          throw new OperationException(
            String.format(
              "Ldap returned result code: %s(%s)",
              code,
              code.intValue()),
            org.ldaptive.ResultCode.valueOf(code.intValue()));
        }
      }
    }
  }


  /**
   * Determines whether to throw operation exception or ldap exception. If
   * operation exception is thrown, the operation will be retried. Otherwise the
   * exception is propagated out.
   *
   * @param  operationRetryResultCodes  to compare result code against
   * @param  e  unbound id exception to examine
   * @param  processor  control processor
   *
   * @throws  OperationException  if the operation should be retried
   * @throws  LdapException  to propagate the exception out
   */
  public static void throwOperationException(
    final org.ldaptive.ResultCode[] operationRetryResultCodes,
    final LDAPException e,
    final ControlProcessor<Control> processor)
    throws LdapException
  {
    final ResultCode rcEnum = e.getResultCode();
    if (
      operationRetryResultCodes != null &&
        operationRetryResultCodes.length > 0) {
      for (org.ldaptive.ResultCode rc : operationRetryResultCodes) {
        if (rc.value() == rcEnum.intValue()) {
          throw new OperationException(
            e,
            org.ldaptive.ResultCode.valueOf(rcEnum.intValue()),
            processor.processResponseControls(null, e.getResponseControls()));
        }
      }
    }
    throw new org.ldaptive.LdapException(
      e,
      org.ldaptive.ResultCode.valueOf(rcEnum.intValue()),
      processor.processResponseControls(null, e.getResponseControls()));
  }


  /**
   * Returns unbound id sort keys using the supplied sort keys.
   *
   * @param  sk  sort keys
   *
   * @return  unbound id sort keys
   */
  public static SortKey[] fromSortKey(
    final org.ldaptive.control.SortKey[] sk)
  {
    SortKey[] keys = null;
    if (sk != null) {
      keys = new SortKey[sk.length];
      for (int i = 0; i < sk.length; i++) {
        keys[i] = new SortKey(
          sk[i].getAttributeDescription(),
          sk[i].getMatchingRuleId(),
          sk[i].getReverseOrder());
      }
    }
    return keys;
  }


  /**
   * Returns the unbound id modification type for the supplied attribute
   * modification type.
   *
   * @param  am  attribute modification type
   *
   * @return  modification type
   */
  protected static ModificationType getModificationType(
    final AttributeModificationType am)
  {
    ModificationType type = null;
    if (am == AttributeModificationType.ADD) {
      type = ModificationType.ADD;
    } else if (am == AttributeModificationType.REMOVE) {
      type = ModificationType.DELETE;
    } else if (am == AttributeModificationType.REPLACE) {
      type = ModificationType.REPLACE;
    }
    return type;
  }
}
