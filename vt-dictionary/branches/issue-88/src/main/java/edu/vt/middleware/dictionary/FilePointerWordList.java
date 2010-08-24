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
import java.util.ArrayList;
import java.util.List;

/**
 * Provides an implementation of {@link WordList} that is backed by a
 * list of file pointers, where each pointer is an offset in the file to a
 * particular word.  This implementation has a smaller memory footprint than
 * {@link ArrayWordList} at the cost of performance.  Each word still must be
 * read from its file for every get.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class FilePointerWordList extends AbstractWordList
{

  /** store all words for a dictionary. */
  protected RandomAccessFile[] files;

  /** data in this word list. */
  protected List<WordReference> references;


  /**
   * Creates a new case-sensitive word list from the supplied file.  The input
   * file is read on initialization and is maintained by this class.
   * <p>
   * <strong>NOTE</strong>
   * <p>
   * Attempts to close any of the source files used this class will cause
   * {@link IOException} when {@link get(index)} is called subsequently.
   *
   * @param  rafs  Array of source word files, where each file has one word
   * per line.
   *
   * @throws  IOException  if an error occurs reading the supplied files
   */
  public FilePointerWordList(final RandomAccessFile[] rafs)
    throws IOException
  {
    this(rafs, true);
  }


  /**
   * Creates a new word list from the supplied file.  The input
   * file is read on initialization and is maintained by this class.
   * <p>
   * <strong>NOTE</strong>
   * <p>
   * Attempts to close any of the source files used this class will cause
   * {@link IOException} when {@link get(index)} is called subsequently.
   *
   * @param  rafs  Array of source word files, where each file has one word
   * per line.
   * @param  caseSensitive  Set to true to create case-sensitive word list,
   * false otherwise.
   *
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FilePointerWordList(
    final RandomAccessFile[] rafs, final boolean caseSensitive)
    throws IOException
  {
    this.files = rafs;
    if (caseSensitive) {
      comparator = CASE_SENSITIVE_COMPARATOR;
    } else {
      comparator = CASE_INSENSITIVE_COMPARATOR;
    }
    this.references = new ArrayList<WordReference>(0);
    for (RandomAccessFile raf : this.files) {
      synchronized (raf) {
        raf.seek(0L);
        this.references.add(new WordReference(raf, 0L));
        while ((raf.readLine()) != null) {
          this.references.add(
            new WordReference(raf, raf.getFilePointer()));
        }
        this.references.remove(this.references.size() - 1);
      }
    }
  }


  /** {@inheritDoc} */
  public String get(final int index)
  {
    this.checkRange(index);
    return this.getWord(this.references.get(index));
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.references.size();
  }


  /**
   * Returns the word associated with the supplied <code>WordReference</code>.
   * See {@link #readFile(RandomAccessFile, long)}.
   *
   * @param  wr  <code>WordReference</code> to read word with
   * @return  <code>String</code> word read from a file
   */
  protected String getWord(final WordReference wr)
  {
    return this.readFile(wr.file, wr.pos);
  }


  /**
   * Returns the files backing this word list.
   *
   * @return  <code>RandomAccessFile[]</code> that are backing this word list
   */
  public RandomAccessFile[] getFiles()
  {
    return this.files;
  }


  /** {@inheritDoc} */
  public void close()
    throws IOException
  {
    for (RandomAccessFile raf : this.files) {
      synchronized (raf) {
        raf.close();
      }
    }
    this.references = null;
  }


  /**
   * Reads the line at the supplied position for the supplied file.
   *
   * @param  raf  <code>RandomAccessFile</code> to read
   * @param  pos  <code>long</code> to seek to in the file
   * @return  <code>String</code> read from the file
   * @throws  IllegalStateException if an error occurs reading the file
   */
  private String readFile(final RandomAccessFile raf, final long pos)
  {
    try {
      synchronized (raf) {
        raf.seek(pos);
        return raf.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file", e);
    }
  }


  /**
   * Object to hold a reference to a file and position for each word.
   */
  private class WordReference
  {

    /** file containing the word. */
    protected RandomAccessFile file;

    /** position of the word in the file. */
    protected long pos;


    /**
     * Creates a new <code>WordReference</code> with the supplied file and
     * position.
     *
     * @param  raf  <code>RandomAccessFile</code> containing the word
     * @param  l  <code>long</code> position of the word in the file
     */
    public WordReference(final RandomAccessFile raf, final long l)
    {
      if (raf == null) {
        throw new NullPointerException("File cannot be null");
      }
      this.file = raf;
      this.pos = l;
    }


    /** {@inheritDoc} */
    public boolean equals(final Object o)
    {
      if (o == this) {
        return true;
      }
      if (!(o instanceof WordReference)) {
        return false;
      }

      final WordReference wr = (WordReference) o;
      return this.file.equals(wr.file) && this.pos == wr.pos;
    }


    /** {@inheritDoc} */
    public int hashCode()
    {
      return this.file.hashCode() + (int) this.pos;
    }
  }
}
