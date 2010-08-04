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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Provides an implementation of a <code>WordList</code> that is backed by a
 * {@link java.util.ArrayList} of strings. Since the entire word list is stored
 * in memory java heap settings may need to be modified in order to store large
 * word lists.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class ArrayWordList extends AbstractWordList
  implements MutableWordList, RandomAccess
{

  /** file containing words. */
  protected List<String> words;


  /**
   * Creates a new <code>ArrayWordList</code> from the supplied list. Each entry
   * in the supplied list is added in sequence to the underlying list.
   *
   * @param  l  <code>List</code> of words to add
   * @throws  NullPointerException if the supplied list is null or contains null
   */
  public ArrayWordList(final List<String> l)
  {
    this.checkNull(l);
    this.words = new ArrayList<String>(l.size());
    for (String s : l) {
      this.checkNull(s);
      this.words.add(s);
    }
  }


  /**
   * See {@link ArrayWordList(List)}.
   *
   * @param  l  <code>List</code> of words to add
   * @param  lc  <code>boolean</code> whether words should be read as lower case
   * @throws  NullPointerException if the supplied list is null or contains null
   */
  public ArrayWordList(final List<String> l, final boolean lc)
  {
    this(l);
    this.setLowerCase(lc);
  }


  /**
   * Creates a new <code>ArrayWordList</code> by reading the supplied readers.
   * Each reader is read line-by-line and added to the underlying list.
   *
   * @param  readers  <code>Reader[]</code> to read
   * @throws  IOException if an error occurs reading
   */
  public ArrayWordList(final Reader[] readers)
    throws IOException
  {
    this.words = new ArrayList<String>();
    for (Reader r : readers) {
      final BufferedReader in = new BufferedReader(r);
      String line;
      while ((line = in.readLine()) != null) {
        this.words.add(line);
      }
      in.close();
    }
  }


  /**
   * See {@link ArrayWordList(Reader[])}.
   *
   * @param  readers  <code>Reader[]</code> to read
   * @param  lc  <code>boolean</code> whether to lower case when reading
   * @throws  IOException if an error occurs reading
   */
  public ArrayWordList(final Reader[] readers, final boolean lc)
    throws IOException
  {
    this(readers);
    this.setLowerCase(lc);
  }


  /** {@inheritDoc} */
  public boolean add(final String s)
  {
    this.checkNull(s);
    return this.words.add(s);
  }


  /** {@inheritDoc} */
  public void add(final int index, final String s)
  {
    this.checkRange(index);
    this.checkNull(s);
    this.words.add(index, s);
  }


  /** {@inheritDoc} */
  public void clear()
  {
    this.words.clear();
  }


  /** {@inheritDoc} */
  public String get(final int index)
  {
    this.checkRange(index);
    final String s = this.words.get(index);
    if (s != null && this.lowerCase) {
      return s.toLowerCase();
    } else {
      return s;
    }
  }


  /** {@inheritDoc} */
  public int indexOf(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    for (int i = 0; i < this.size(); i++) {
      if (o.equals(this.get(i))) {
        return i;
      }
    }
    return -1;
  }


  /** {@inheritDoc} */
  public Iterator<String> iterator()
  {
    return this.words.iterator();
  }


  /** {@inheritDoc} */
  public int lastIndexOf(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    for (int i = this.size() - 1; i >= 0; i--) {
      if (o.equals(this.get(i))) {
        return i;
      }
    }
    return -1;
  }


  /** {@inheritDoc} */
  public ListIterator<String> listIterator()
  {
    return this.words.listIterator();
  }


  /** {@inheritDoc} */
  public ListIterator<String> listIterator(final int index)
  {
    return this.words.listIterator(index);
  }


  /** {@inheritDoc} */
  public String remove(final int index)
  {
    this.checkRange(index);
    final String s = this.words.remove(index);
    if (s != null && this.lowerCase) {
      return s.toLowerCase();
    } else {
      return s;
    }
  }


  /** {@inheritDoc} */
  public boolean remove(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    return this.words.remove(o);
  }


  /** {@inheritDoc} */
  public boolean removeAll(final Collection<?> c)
  {
    this.checkNull(c);
    boolean modified = false;
    for (Object o : c) {
      if (this.remove(o)) {
        modified = true;
      }
    }
    return modified;
  }


  /** {@inheritDoc} */
  public boolean retainAll(final Collection<?> c)
  {
    this.checkNull(c);
    return this.words.retainAll(c);
  }


  /** {@inheritDoc} */
  public String set(final int index, final String word)
  {
    this.checkRange(index);
    final String s = this.words.set(index, word);
    if (s != null && this.lowerCase) {
      return s.toLowerCase();
    } else {
      return s;
    }
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.words.size();
  }


  /** {@inheritDoc} */
  public ArrayWordList subList(final int fromIndex, final int toIndex)
  {
    return new ArrayWordList(
      this.words.subList(fromIndex, toIndex),
      this.lowerCase);
  }


  /**
   * Returns the words backing this list.
   *
   * @return  <code>List</code> that is backing this list
   */
  public List<String> getWords()
  {
    return this.words;
  }


  /** {@inheritDoc} */
  public void close()
    throws IOException
  {
    this.words = null;
  }
}
