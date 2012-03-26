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
package org.ldaptive.handler;

import org.ldaptive.LdapEntry;
import org.ldaptive.LdapUtils;
import org.ldaptive.SearchRequest;

/**
 * Provides the ability to modify the case of ldap entry DNs, attribute names,
 * and attribute values.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CaseChangeEntryHandler extends AbstractLdapEntryHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 821;

  /** Enum to define the type of case change. */
  public enum CaseChange {

    /** no case change. */
    NONE,

    /** lower case. */
    LOWER,

    /** upper case. */
    UPPER;


    /**
     * This changes the supplied string based on the supplied case change.
     *
     * @param  cc  case change to perform
     * @param  string  to modify
     *
     * @return  string that has been changed
     */
    public static String perform(final CaseChange cc, final String string)
    {
      String s = null;
      if (CaseChange.LOWER == cc) {
        s = string.toLowerCase();
      } else if (CaseChange.UPPER == cc) {
        s = string.toUpperCase();
      } else if (CaseChange.NONE == cc) {
        s = string;
      }
      return s;
    }
  }

  /** Type of case modification to make to the entry DN. */
  private CaseChange dnCaseChange = CaseChange.NONE;

  /** Type of case modification to make to the attribute names. */
  private CaseChange attributeNameCaseChange = CaseChange.NONE;

  /** Type of case modification to make to the attributes values. */
  private CaseChange attributeValueCaseChange = CaseChange.NONE;


  /**
   * Returns the DN case change.
   *
   * @return  case change
   */
  public CaseChange getDnCaseChange()
  {
    return dnCaseChange;
  }


  /**
   * Sets the DN case change.
   *
   * @param  cc  case change
   */
  public void setDnCaseChange(final CaseChange cc)
  {
    dnCaseChange = cc;
  }


  /**
   * Returns the attribute name case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeNameCaseChange()
  {
    return attributeNameCaseChange;
  }


  /**
   * Sets the attribute name case change.
   *
   * @param  cc  case change
   */
  public void setAttributeNameCaseChange(final CaseChange cc)
  {
    attributeNameCaseChange = cc;
  }


  /**
   * Returns the attribute value case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeValueCaseChange()
  {
    return attributeValueCaseChange;
  }


  /**
   * Sets the attribute value case change.
   *
   * @param  cc  case change
   */
  public void setAttributeValueCaseChange(final CaseChange cc)
  {
    attributeValueCaseChange = cc;
  }


  /** {@inheritDoc} */
  @Override
  protected String processDn(
    final SearchRequest request,
    final LdapEntry entry)
  {
    return CaseChange.perform(dnCaseChange, entry.getDn());
  }


  /** {@inheritDoc} */
  @Override
  protected String processAttributeName(
    final SearchRequest request,
    final String name)
  {
    return CaseChange.perform(attributeNameCaseChange, name);
  }


  /** {@inheritDoc} */
  @Override
  protected String processAttributeValue(
    final SearchRequest request,
    final String value)
  {
    return CaseChange.perform(attributeValueCaseChange, value);
  }


  /** {@inheritDoc} */
  @Override
  protected byte[] processAttributeValue(
    final SearchRequest request,
    final byte[] value)
  {
    return value;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtils.computeHashCode(
        HASH_CODE_SEED,
        dnCaseChange,
        attributeNameCaseChange,
        attributeValueCaseChange);
  }
}
