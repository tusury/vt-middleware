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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.TreeMap;

/**
 * Provides an implementation of a <code>WordList</code> that is backed by a
 * file. Provides a smaller memory footprint than {@link FilePointerWordList} at
 * the cost of performance. Each word still must be read from the file for
 * every get. This implementation only supports a single file as that file
 * should be sorted before it is read. By default the cache size is 5% of the
 * file. This value can be tweak to improve performance at the cost of memory.
 * All operations that attempt to modify this list throw
 * <code>UnsupportedOperationException</code>.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class FileWordList extends AbstractWordList implements RandomAccess
{

  /** default cache size. */
  public static final double DEFAULT_CACHE_SIZE = 0.05;

  /** file containing words. */
  protected RandomAccessFile file;

  /** size of the file. */
  protected int size;

  /** cache of indexes to file positions. */
  protected TreeMap<Integer, Long> cache = new TreeMap<Integer, Long>();


  /**
   * Creates a new <code>FileWordList</code> with the supplied file. The file is
   * immediately read in order to store it's size and initialize the cache. See
   * {@link #intializeCache(double)}.
   *
   * @param  raf  <code>RandomAccessFile</code> to read
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FileWordList(final RandomAccessFile raf)
    throws IOException
  {
    this(raf, false);
  }


  /**
   * Creates a new <code>FileWordList</code> with the supplied file and the
   * supplied lower case property. The file is immediately read in order to
   * store it's size and initialize the cache. See
   * {@link #intializeCache(double)}.
   *
   * @param  raf  <code>RandomAccessFile</code> to read
   * @param  lc  <code>boolean</code> whether to lower case when reading
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FileWordList(final RandomAccessFile raf, final boolean lc)
    throws IOException
  {
    this(raf, lc, DEFAULT_CACHE_SIZE);
  }


  /**
   * Creates a new <code>FileWordList</code> with the supplied file, lower case
   * property and cache percent. The file is immediately read in order to store
   * it's size and initialize the cache. See {@link #intializeCache(double)}.
   *
   * @param  raf  <code>RandomAccessFile</code> to read
   * @param  lc  <code>boolean</code> whether to lower case when reading
   * @param  cachePercent  <code>double</code> percentage of file to cache
   * @throws  IllegalArgumentException  if cachePercent is less than 0 or
   * greater than 1
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FileWordList(
    final RandomAccessFile raf, final boolean lc, final double cachePercent)
    throws IOException
  {
    if (cachePercent < 0 || cachePercent > 1) {
      throw new IllegalArgumentException(
        "cachePercent must be between 0 and 1 inclusive");
    }
    this.file = raf;
    synchronized (this.file) {
      this.file.seek(0L);
      while ((this.file.readLine()) != null) {
        this.size++;
      }
      this.intializeCache(cachePercent);
    }
    this.setLowerCase(lc);
  }


  /**
   * Reads the underlying file to cache the supplied percentage of line
   * positions.
   *
   * @param  cachePercent  <code>double</code> number between 0 and 1 that
   * represents the percentage of the file to cache
   * @throws  IOException  if an error occurs reading the supplied file
   */
  private void intializeCache(final double cachePercent)
    throws IOException
  {
    final int cacheSize = (int) (this.size * cachePercent);
    if (cacheSize > 0) {
      final int offset = cacheSize > this.size ? 1 : this.size / cacheSize;
      long pos = 0L;
      this.file.seek(pos);
      for (int i = 0; i < this.size; i++) {
        this.file.readLine();
        if (i != 0 && i % offset == 0) {
          this.cache.put(i, pos);
        }
        pos = this.file.getFilePointer();
      }
    }
  }


  /** {@inheritDoc} */
  public boolean add(final String s)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public void add(final int index, final String s)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public boolean addAll(final Collection<? extends String> c)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public boolean addAll(final int index, final Collection<? extends String> c)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public void clear()
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public String get(final int index)
  {
    this.checkRange(index);
    return this.readFile(index);
  }


  /** {@inheritDoc} */
  public int indexOf(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    return this.readFile((String) o, false);
  }


  /** {@inheritDoc} */
  public Iterator<String> iterator()
  {
    return new FileWordIterator();
  }


  /** {@inheritDoc} */
  public int lastIndexOf(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    return this.readFile((String) o, true);
  }


  /** {@inheritDoc} */
  public ListIterator<String> listIterator()
  {
    return new FileWordListIterator(0);
  }


  /** {@inheritDoc} */
  public ListIterator<String> listIterator(final int index)
  {
    this.checkRange(index);
    return new FileWordListIterator(index);
  }


  /** {@inheritDoc} */
  public String remove(final int index)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public boolean remove(final Object o)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public boolean removeAll(final Collection<?> c)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public boolean retainAll(final Collection<?> c)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public String set(final int index, final String s)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.size;
  }


  /** {@inheritDoc} */
  public FileWordList subList(final int fromIndex, final int toIndex)
  {
    throw new UnsupportedOperationException("Operation not supported");
  }


  /**
   * Returns the file backing this list.
   *
   * @return  <code>RandomAccessFile</code> that is backing this list
   */
  public RandomAccessFile getFile()
  {
    return this.file;
  }


  /** {@inheritDoc} */
  public void close()
    throws IOException
  {
    synchronized (this.file) {
      this.file.close();
    }
    this.cache = null;
  }


  /**
   * Reads the file line by line and returns an index of the supplied
   * word. Returns -1 if the word cannot be found. This is an expensive
   * operation as the file is read line-by-line from it's beginning until the
   * word is found.
   *
   * @param  word  <code>String</code> to search for
   * @param  lastIndex  <code>boolean</code> whether the last index should be
   * returned
   * @return  <code>int</code> index of the supplied word in the file
   * @throws  IllegalStateException  if an error occurs reading the supplied
   * file
   */
  private int readFile(final String word, final boolean lastIndex)
  {
    int index = -1;
    try {
      synchronized (this.file) {
        int i = 0;
        this.file.seek(0L);
        String s;
        while ((s = this.file.readLine()) != null) {
          if (this.lowerCase) {
            s = s.toLowerCase();
          }
          if (s.equals(word)) {
            index = i;
            if (!lastIndex) {
              break;
            }
          }
          i++;
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file", e);
    }
    return index;
  }


  /**
   * Reads the file line by line and returns the word at the supplied index.
   * Returns null if the index cannot be read. This method leverages the cache
   * to seek to the closest position of the supplied index.
   *
   * @param  index  <code>int</code> to read word at
   * @return  <code>String</code> word at the supplied index
   * @throws  IllegalStateException  if an error occurs reading the supplied
   * file
   */
  private String readFile(final int index)
  {
    try {
      synchronized (this.file) {
        int i = 0;
        if (!this.cache.isEmpty() && this.cache.firstKey() <= index) {
          i = this.cache.floorKey(index);
        }
        final long pos = i > 0 ? this.cache.get(i) : 0L;
        this.file.seek(pos);
        String s;
        while ((s = this.file.readLine()) != null) {
          if (i == index) {
            return lowerCase ? s.toLowerCase() : s;
          }
          i++;
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file", e);
    }
    return null;
  }


  /**
   * Iterator implementation for this word list.
   */
  private class FileWordIterator implements Iterator<String>
  {

    /** index of element to be returned by subsequent call to next. */
    protected int cursor;


    /** {@inheritDoc} */
    public boolean hasNext()
    {
      return cursor != FileWordList.this.size();
    }


    /** {@inheritDoc} */
    public String next()
    {
      try {
        final String word = FileWordList.this.get(cursor);
        cursor++;
        return word;
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    }


    /** {@inheritDoc} */
    public void remove()
    {
      throw new UnsupportedOperationException("Operation not supported");
    }
  }


  /**
   * ListIterator implementation for this word list.
   */
  private class FileWordListIterator extends FileWordIterator
    implements ListIterator<String>
  {


    /**
     * Creates a new <code>FileWordListIterator</code> with the supplied index.
     *
     * @param  index  <code>int</code> to set the cursor at
     */
    public FileWordListIterator(final int index)
    {
      this.cursor = index;
    }


    /** {@inheritDoc} */
    public boolean hasPrevious()
    {
      return this.cursor != 0;
    }


    /** {@inheritDoc} */
    public int nextIndex()
    {
      return this.cursor;
    }


    /** {@inheritDoc} */
    public String previous()
    {
      try {
        final String word = FileWordList.this.get(cursor - 1);
        this.cursor--;
        return word;
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    }


    /** {@inheritDoc} */
    public int previousIndex()
    {
      return this.cursor - 1;
    }


    /** {@inheritDoc} */
    public void set(final String s)
    {
      throw new UnsupportedOperationException("Operation not supported");
    }


    /** {@inheritDoc} */
    public void add(final String s)
    {
      throw new UnsupportedOperationException("Operation not supported");
    }
  }
}
