/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

import java.util.Arrays;

/**
 * Representation of the <code>NoticeReference</code> type defined in section
 * 4.2.1.5 of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class NoticeReference
{

  /** Hash code scale factor. */
  private static final int HASH_FACTOR = 31;

  /** Organization name. */
  private String organization;

  /**
   * Notice numbers that typically identify blocks of text within a document.
   */
  private int[] noticeNumbers;


  /**
   * Creates a new notice reference with the supplied parameters.
   *
   * @param  org  Organization name.
   * @param  numbers  Notice numbers that typically identify locations in a
   * referenced document belonging to the organization.
   */
  public NoticeReference(final String org, final int[] numbers)
  {
    if (org == null || "".equals(org)) {
      throw new IllegalArgumentException(
        "Organization cannot be null or empty string.");
    }
    organization = org;

    if (numbers == null || numbers.length == 0) {
      throw new IllegalArgumentException(
        "Notice numbers cannot be null and must contain at least one element.");
    }
    noticeNumbers = numbers;
  }


  /** @return  Organization name associated with the reference. */
  public String getOrganization()
  {
    return organization;
  }


  /**
   * @return  Array of integers that typically identify locations in a document
   * belonging to the organization. A common use case is for application
   * software to display the blocks of text from a referenced document at
   * locations specified by this field.
   */
  public int[] getNoticeNumbers()
  {
    return noticeNumbers;
  }


  /** @return  Tuple of the organization name and notice numbers. */
  @Override
  public String toString()
  {
    return
      String.format("(%s, %s)", organization, Arrays.toString(noticeNumbers));
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      final NoticeReference other = (NoticeReference) obj;
      result = other.getOrganization().equals(organization) &&
        Arrays.equals(other.getNoticeNumbers(), noticeNumbers);
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    hash = HASH_FACTOR * hash + organization.hashCode();
    hash = HASH_FACTOR * hash + Arrays.hashCode(noticeNumbers);
    return hash;
  }
}
