/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509.types;

/**
 * Representation of the <code>UserNotice</code> type defined in section 4.2.1.5
 * of RFC 2459.
 *
 * @author  Middleware Services
 * @version  $Revision: 427 $
 */
public class UserNotice
{

  /** Hash code scale factor. */
  private static final int HASH_FACTOR = 31;

  /** Optional notice reference. */
  private NoticeReference noticeRef;

  /** Optional text. */
  private String explicitText;


  /**
   * Creates an empty user notice. Although this is technically supported by RFC
   * 2459, an empty user notice is meaningless. We support it here to be
   * strictly conformant with the RFC.
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
   * Creates a new instance with the given notice reference and explicit display
   * text.
   *
   * @param  ref  Notice reference.
   * @param  text  Explicit display text.
   */
  public UserNotice(final NoticeReference ref, final String text)
  {
    noticeRef = ref;
    explicitText = text;
  }


  /** @return  The notice reference if defined otherwise null. */
  public NoticeReference getNoticeRef()
  {
    return noticeRef;
  }


  /** @return  Explicit text for display if defined otherwise null. */
  public String getExplicitText()
  {
    return explicitText;
  }


  /**
   * @return  String representation containing the ExplicitText and
   * NoticeReference fields.
   */
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
      result = (noticeRef != null ? noticeRef.equals(other.getNoticeRef())
                                  : other.getNoticeRef() == null) &&
          (explicitText != null ? noticeRef.equals(other.getNoticeRef())
                                : other.getNoticeRef() == null);
    }
    return result;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hash = getClass().hashCode();
    if (noticeRef != null) {
      hash = HASH_FACTOR * hash + noticeRef.hashCode();
    }
    if (explicitText != null) {
      hash = HASH_FACTOR * hash + explicitText.hashCode();
    }
    return hash;
  }
}
