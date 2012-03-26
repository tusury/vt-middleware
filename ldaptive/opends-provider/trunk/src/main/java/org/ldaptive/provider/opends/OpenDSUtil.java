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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.OperationException;
import org.ldaptive.SortBehavior;
import org.ldaptive.provider.ControlProcessor;
import org.opends.sdk.Attribute;
import org.opends.sdk.ByteString;
import org.opends.sdk.ByteStringBuilder;
import org.opends.sdk.DN;
import org.opends.sdk.Entry;
import org.opends.sdk.ErrorResultException;
import org.opends.sdk.LinkedAttribute;
import org.opends.sdk.LinkedHashMapEntry;
import org.opends.sdk.Modification;
import org.opends.sdk.ModificationType;
import org.opends.sdk.ResultCode;
import org.opends.sdk.SortKey;
import org.opends.sdk.controls.Control;

/**
 * Provides methods for converting between OpenDS specific objects and
 * ldaptive specific objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDSUtil
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
  public OpenDSUtil()
  {
    sortBehavior = SortBehavior.getDefaultSortBehavior();
  }


  /**
   * Creates a new opends util.
   *
   * @param  sb  sort behavior
   */
  public OpenDSUtil(final SortBehavior sb)
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
   * Returns an opends byte string that represents the values in the supplied
   * collection.
   *
   * @param  values  to convert to byte strings
   *
   * @return  byte strings
   */
  public ByteString[] fromStringValues(final Collection<String> values)
  {
    final ByteString[] bstrings = new ByteString[values.size()];
    int i = 0;
    for (String s : values) {
      final ByteStringBuilder builder = new ByteStringBuilder(s.length());
      builder.append(s);
      bstrings[i++] = builder.toByteString();
    }
    return bstrings;
  }


  /**
   * Returns an opends byte string that represents the values in the supplied
   * collection.
   *
   * @param  values  to convert to byte strings
   *
   * @return  byte strings
   */
  public ByteString[] fromBinaryValues(final Collection<byte[]> values)
  {
    final ByteString[] bstrings = new ByteString[values.size()];
    int i = 0;
    for (byte[] b : values) {
      final ByteStringBuilder builder = new ByteStringBuilder(b.length);
      builder.append(b);
      bstrings[i++] = builder.toByteString();
    }
    return bstrings;
  }


  /**
   * Returns string values for the supplied byte strings.
   *
   * @param  values  to convert to strings
   *
   * @return  string values
   */
  public String[] toStringValues(final ByteString[] values)
  {
    final String[] strings = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      strings[i] = values[i].toString();
    }
    return strings;
  }


  /**
   * Returns byte array values for the supplied byte strings.
   *
   * @param  values  to convert to byte arrays
   *
   * @return  byte array values
   */
  public byte[][] toBinaryValues(final ByteString[] values)
  {
    final byte[][] bytes = new byte[values.length][];
    for (int i = 0; i < values.length; i++) {
      bytes[i] = values[i].toByteArray();
    }
    return bytes;
  }


  /**
   * Returns an opends attribute that represents the values in the supplied
   * ldap attribute.
   *
   * @param  la  ldap attribute
   *
   * @return  opends attribute
   */
  public Attribute fromLdapAttribute(final LdapAttribute la)
  {
    Attribute attribute = null;
    if (la.isBinary()) {
      attribute = new LinkedAttribute(
        la.getName(), (Object[]) fromBinaryValues(la.getBinaryValues()));
    } else {
      attribute = new LinkedAttribute(
        la.getName(), (Object[]) fromStringValues(la.getStringValues()));
    }
    return attribute;
  }


  /**
   * Returns an ldap attribute using the supplied opends attribute.
   *
   * @param  a  opends attribute
   *
   * @return  ldap attribute
   */
  public LdapAttribute toLdapAttribute(final Attribute a)
  {
    boolean isBinary = false;
    if (a.getAttributeDescriptionAsString().indexOf(";binary") != -1) {
      isBinary = true;
    } else if (binaryAttrs != null &&
               binaryAttrs.contains(a.getAttributeDescriptionAsString())) {
      isBinary = true;
    }

    if (!isBinary) {
      final String oid =
        a.getAttributeDescription().getAttributeType().getOID();
      isBinary = "1.3.6.1.4.1.1466.115.121.1.5".equals(oid);
    }

    LdapAttribute la = null;
    if (isBinary) {
      la = new LdapAttribute(sortBehavior, true);
      la.setName(a.getAttributeDescriptionAsString());
      la.addBinaryValue(toBinaryValues(a.toArray()));
    } else {
      la = new LdapAttribute(sortBehavior, false);
      la.setName(a.getAttributeDescriptionAsString());
      la.addStringValue(toStringValues(a.toArray()));
    }
    return la;
  }


  /**
   * Returns a list of opends attribute that represents the values in the
   * supplied ldap attributes.
   *
   * @param  c  ldap attributes
   *
   * @return  opends attributes
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
   * Returns an opends entry that represents the values in the supplied entry.
   *
   * @param  le  ldap entry
   *
   * @return  opends entry
   */
  public Entry fromLdapEntry(final LdapEntry le)
  {
    final Entry entry = new LinkedHashMapEntry();
    entry.setName(DN.valueOf(le.getDn()));
    for (LdapAttribute la : le.getAttributes()) {
      entry.addAttribute(fromLdapAttribute(la), null);
    }
    return entry;
  }


  /**
   * Returns an ldap entry using the supplied opends entry.
   *
   * @param  e  opends entry
   *
   * @return  ldap entry
   */
  public LdapEntry toLdapEntry(final Entry e)
  {
    final LdapEntry le = new LdapEntry(sortBehavior);
    le.setDn(e.getName().toString());
    for (Attribute a : e.getAllAttributes()) {
      le.addAttribute(toLdapAttribute(a));
    }
    return le;
  }


  /**
   * Returns opends modifications using the supplied attribute
   * modifications.
   *
   * @param  am  attribute modifications
   *
   * @return  opends modifications
   */
  public Modification[] fromAttributeModification(
    final AttributeModification[] am)
  {
    final Modification[] mods = new Modification[am.length];
    for (int i = 0; i < am.length; i++) {
      final Attribute a = fromLdapAttribute(am[i].getAttribute());
      if (am[i].getAttribute().isBinary()) {
        mods[i] = new Modification(
          getModificationType(am[i].getAttributeModificationType()), a);
      } else {
        mods[i] = new Modification(
          getModificationType(am[i].getAttributeModificationType()), a);
      }
    }
    return mods;
  }


  /**
   * Determines whether to throw operation exception or do nothing. If operation
   * exception is thrown, the operation will be retried.
   *
   * @param  operationRetryResultCodes  to compare result code against
   * @param  code  opends result code to examine
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
   * @param  e  opends exception to examine
   * @param  processor  control processor
   *
   * @throws  OperationException  if the operation should be retried
   * @throws  LdapException  to propagate the exception out
   */
  public static void throwOperationException(
    final org.ldaptive.ResultCode[] operationRetryResultCodes,
    final ErrorResultException e,
    final ControlProcessor<Control> processor)
    throws LdapException
  {
    final ResultCode rcEnum = e.getResult().getResultCode();
    if (
      operationRetryResultCodes != null &&
        operationRetryResultCodes.length > 0) {
      for (org.ldaptive.ResultCode rc : operationRetryResultCodes) {
        if (rc.value() == rcEnum.intValue()) {
          throw new OperationException(
            e,
            org.ldaptive.ResultCode.valueOf(rcEnum.intValue()),
            processor.processResponseControls(
              null, e.getResult().getControls().toArray(new Control[0])));
        }
      }
    }
    throw new org.ldaptive.LdapException(
      e,
      org.ldaptive.ResultCode.valueOf(rcEnum.intValue()),
      processor.processResponseControls(
        null, e.getResult().getControls().toArray(new Control[0])));
  }


  /**
   * Returns opends sort keys using the supplied sort keys.
   *
   * @param  sk  sort keys
   *
   * @return  opends sort keys
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
          sk[i].getReverseOrder(),
          sk[i].getMatchingRuleId());
      }
    }
    return keys;
  }


  /**
   * Returns the opends modification type for the supplied attribute
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
