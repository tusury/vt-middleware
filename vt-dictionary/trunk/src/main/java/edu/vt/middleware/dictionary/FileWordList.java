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
import java.util.TreeMap;

/**
 * Provides an implementation of a {@link WordList} that is backed by a
 * file. Each word is read from the file for every get, though the
 * implementation supports a simple memory cache to improve read performance.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class FileWordList extends AbstractWordList
{

  /** default cache size. */
  public static final int DEFAULT_CACHE_SIZE = 5;

  /** 100 percent */
  private static final int HUNDRED_PERCENT = 100;

  /** file containing words. */
  protected RandomAccessFile file;

  /** size of the file. */
  protected int size;

  /** cache of indexes to file positions. */
  protected TreeMap<Integer, Long> cache = new TreeMap<Integer, Long>();


  /**
   * Creates a new case-sensitive word list from the supplied file.  The input
   * file is read on initialization and is maintained by this class.
   * <p>
   * <strong>NOTE</strong>
   * Attempts to close the source file will cause {@link IOException} when
   * {@link #get(int)} is called subsequently.
   * </p>
   *
   * @param  raf  File containing words, one per line.
   *
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FileWordList(final RandomAccessFile raf)
    throws IOException
  {
    this(raf, true);
  }


  /**
   * Creates a new word list from the supplied file.  The input
   * file is read on initialization and is maintained by this class.
   * <p>
   * <strong>NOTE</strong>
   * Attempts to close the source file will cause {@link IOException} when
   * {@link #get(int)} is called subsequently.
   * </p>
   *
   * @param  raf  File containing words, one per line.
   * @param  caseSensitive  Set to true to create case-sensitive word list,
   * false otherwise.
   *
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FileWordList(final RandomAccessFile raf, final boolean caseSensitive)
    throws IOException
  {
    this(raf, caseSensitive, DEFAULT_CACHE_SIZE);
  }


  /**
   * Creates a new word list from the supplied file.  The input
   * file is read on initialization and is maintained by this class.
   * <p>
   * <strong>NOTE</strong>
   * Attempts to close the source file will cause {@link IOException} when
   * {@link #get(int)} is called subsequently.
   * </p>
   *
   * @param  raf  File containing words, one per line.
   * @param  caseSensitive  Set to true to create case-sensitive word list,
   * false otherwise.
   * @param  cachePercent  Percent (0-100) of file to cache in memory for
   * improved read performance.
   *
   * @throws  IllegalArgumentException  if cache percent is out of range.
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FileWordList(
    final RandomAccessFile raf,
    final boolean caseSensitive,
    final int cachePercent)
    throws IOException
  {
    if (cachePercent < 0 || cachePercent > HUNDRED_PERCENT) {
      throw new IllegalArgumentException(
        "cachePercent must be between 0 and 100 inclusive");
    }
    this.file = raf;
    if (caseSensitive) {
      this.comparator = WordLists.CASE_SENSITIVE_COMPARATOR;
    } else {
      this.comparator = WordLists.CASE_INSENSITIVE_COMPARATOR;
    }
    synchronized (this.file) {
      this.file.seek(0L);
      String a = null;
      String b = null;
      while ((a = this.file.readLine()) != null) {
        if (a != null && b != null && this.comparator.compare(a, b) < 0) {
          throw new IllegalArgumentException(
            "File is not sorted correctly for this comparator");
        }
        b = a;
        this.size++;
      }
      this.intializeCache(cachePercent * this.size / HUNDRED_PERCENT);
    }
  }


  /**
   * Reads the underlying file to cache the supplied percentage of line
   * positions.
   *
   * @param  cacheSize  Number of entries in cache.
   *
   * @throws  IOException  if an error occurs reading the supplied file
   */
  private void intializeCache(final int cacheSize)
    throws IOException
  {
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
  public String get(final int index)
  {
    this.checkRange(index);
    return this.readFile(index);
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.size;
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


  /**
   * Closes the underlying file and make the cache available for garbage
   * collection.
   *
   * @throws  IOException  if an error occurs closing the file
   */
  public void close()
    throws IOException
  {
    synchronized (this.file) {
      this.file.close();
    }
    this.cache = null;
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
          if (this.cache.containsKey(index)) {
            i = index;
          } else {
            i = this.cache.headMap(index).lastKey();
          }
        }
        final long pos = i > 0 ? this.cache.get(i) : 0L;
        this.file.seek(pos);
        String s;
        while ((s = this.file.readLine()) != null) {
          if (i == index) {
            return s;
          }
          i++;
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file", e);
    }
    return null;
  }
}
