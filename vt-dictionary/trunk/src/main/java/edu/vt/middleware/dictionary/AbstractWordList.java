/*
  $Id: Dictionary.java 1252 2010-04-16 21:24:23Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1252 $
  Updated: $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
*/
package edu.vt.middleware.dictionary;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides common {@link java.util.List} implementations for word lists. The
 * lower case property allows data to be read from the list in lower case
 * format, however the underlying data is not changed. This allows lists to be
 * sorted as if they are lower case without modifying the data.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public abstract class AbstractWordList implements WordList
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 31;

  /** whether to return words as lower case. */
  protected boolean lowerCase;


  /** {@inheritDoc} */
  public void setLowerCase(final boolean b)
  {
    this.lowerCase = b;
  }


  /** {@inheritDoc} */
  public boolean isLowerCase()
  {
    return this.lowerCase;
  }


  /** {@inheritDoc} */
  public boolean addAll(final Collection<? extends String> c)
  {
    this.checkNull(c);
    boolean modified = false;
    for (String s : c) {
      if (this.add(s)) {
        modified = true;
      }
    }
    return modified;
  }


  /** {@inheritDoc} */
  public boolean addAll(final int index, final Collection<? extends String> c)
  {
    this.checkNull(c);
    boolean modified = false;
    int i = 0;
    for (String s : c) {
      this.add(index + i, s);
      modified = true;
      i++;
    }
    return modified;
  }


  /** {@inheritDoc} */
  public boolean contains(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    return this.indexOf(o) >= 0;
  }


  /** {@inheritDoc} */
  public boolean containsAll(final Collection<?> c)
  {
    this.checkNull(c);
    for (Object o : c) {
      if (!this.contains((String) o)) {
        return false;
      }
    }
    return true;
  }


  /** {@inheritDoc} */
  public boolean equals(final Object o)
  {
    if (o == this) {
      return true;
    }
    if (!(o instanceof List<?>)) {
      return false;
    }

    final ListIterator<String> e1 = this.listIterator();
    final ListIterator<?> e2 = ((List<?>) o).listIterator();
    while (e1.hasNext() && e2.hasNext()) {
      final String o1 = e1.next();
      final Object o2 = e2.next();
      if (!(o1 == null ? o2 == null : o1.equals(o2))) {
        return false;
      }
    }
    return !(e1.hasNext() || e2.hasNext());
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hash = 1;
    final Iterator<String> i = this.iterator();
    while (i.hasNext()) {
      final String s = i.next();
      hash = HASH_CODE_SEED * hash + (s == null ? 0 : s.hashCode());
    }
    return hash;
  }


  /** {@inheritDoc} */
  public boolean isEmpty()
  {
    return this.size() == 0;
  }


  /** {@inheritDoc} */
  public Object[] toArray()
  {
    return this.toArray(new String[this.size()]);
  }


  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(final T[] a)
  {
    if (!String[].class.isInstance(a)) {
      throw new ArrayStoreException("Parameter must be a supertype String[]");
    }

    T[] s = null;
    if (a.length < this.size()) {
      s = (T[]) java.lang.reflect.Array.newInstance(
        a.getClass().getComponentType(), this.size());
    } else {
      s = a;
    }

    for (int i = 0; i < this.size(); i++) {
      s[i] = (T) this.get(i);
    }

    if (s.length > this.size()) {
      s[this.size()] = null;
    }
    return s;
  }


  /**
   * Returns a string representation of this word list.
   *
   * @return  <code>String</code> list of words
   */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < this.size(); i++) {
      sb.append(this.get(i)).append(", ");
    }
    sb.append("]");
    return sb.toString();
  }


  /**
   * Throws a <code>NullPointerException</code> if the supplied object is null.
   *
   * @param  o  <code>Object</code>
   */
  protected void checkNull(final Object o)
  {
    if (o == null) {
      throw new NullPointerException("Parameter cannot be null");
    }
  }


  /**
   * Throws a <code>IndexOutOfBoundsException</code> if the supplied index is
   * less than 0 or greater than or equal to the size of this word list.
   *
   * @param  index  <code>int</code>
   */
  protected void checkRange(final int index)
  {
    if (index < 0 || index >= this.size()) {
      throw new IndexOutOfBoundsException(
        "Supplied index (" + index + ") does not exist");
    }
  }


  /**
   * Throws a <code>ClassCastException</code> if the supplied object is not an
   * instance of <code>String</code>.
   *
   * @param  o  <code>Object</code>
   */
  protected void checkIsString(final Object o)
  {
    if (!String.class.isInstance(o)) {
      throw new ClassCastException("Parameter must be of type String");
    }
  }
}
