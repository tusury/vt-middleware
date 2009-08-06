/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of UserNotice type defined in RFC 2459.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class UserNotice
{
  /** Hash code seed value */
  private static final int HASH_SEED = 37;

  /** Hash code scale factor */
  private static final int HASH_FACTOR = 31;

  /** Optional notice reference */
  private NoticeReference noticeRef;

  /** Optional text */
  private String explicitText;


  /**
   * Creates a new instance with notice reference and explicit display text
   * undefined.
   */
  public UserNotice() {}


  /**
   * Creates a new instance with the given notice reference.
   *
   * @param  ref  Notice reference.
   */
  public UserNotice(final NoticeReference ref)
  {
    this(ref, null);
  }


  /**
   * Creates a new instance with the given explicit display text.
   *
   * @param  text  Explicit display text.
   */
  public UserNotice(final String text)
  {
    this(null, text);
  }


  /**
   * Creates a new instance with the given notice reference and explicit
   * display text.
   *
   * @param  ref  Notice reference.
   * @param  text  Explicit display text.
   */
  public UserNotice(final NoticeReference ref, final String text)
  {
    noticeRef = ref;
    explicitText = text;
  }


  /**
   * @return  The notice reference if defined otherwise null.
   */
  public NoticeReference getNoticeRef()
  {
    return noticeRef;
  }


  /**
   * @return  Explicit text for display if defined otherwise null.
   */
  public String getExplicitText()
  {
    return explicitText;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    int count = 0;
    if (explicitText != null) {
      ++count;
      sb.append(explicitText);
    }
    if (noticeRef != null) {
      if (++count > 1) {
        sb.append(", ");
      }
      sb.append("NoticeReference:");
      sb.append(noticeRef);
    }
    return sb.toString();
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj)
  {
    boolean result = false;
    if (obj == this) {
      result = true;
    } else if (obj == null || obj.getClass() != getClass()) {
      result = false;
    } else {
      final UserNotice other = (UserNotice) obj;
      if (noticeRef != null) {
        result &= noticeRef.equals(other.getNoticeRef());
      } else {
        result &= other.getNoticeRef() == null;
      }
      if (explicitText != null) {
        result &= explicitText.equals(other.getExplicitText());
      } else {
        result &= other.getExplicitText() == null;
      }
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = HASH_SEED;
    if (noticeRef != null) {
      hash = HASH_FACTOR * hash + noticeRef.hashCode();
    }
    if (explicitText != null) {
      hash = HASH_FACTOR * hash + explicitText.hashCode();
    }
    return hash;
  }
}
