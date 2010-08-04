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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Provides an implementation of a <code>WordList</code> that is backed by a
 * list of file pointers. Provides a smaller memory footprint than
 * {@link ArrayWordList} at the cost of performance. Each word still must be
 * read from it's file for every get. This implementation allows in memory
 * sorting of multiple files. Modifications to this list are supported as long
 * as the words added or removed exist in the files.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class FilePointerWordList extends AbstractWordList
  implements MutableWordList, RandomAccess
{

  /** store all words for a dictionary. */
  protected RandomAccessFile[] files;

  /** data in this word list. */
  protected List<WordReference> references;


  /**
   * Creates a new <code>FileWordList</code> with the supplied files. The files
   * are immediately read in order to store the position of every word in each
   * file.
   *
   * @param  rafs  <code>RandomAccessFile[]</code> to read
   * @throws  IOException  if an error occurs reading the supplied files
   */
  public FilePointerWordList(final RandomAccessFile[] rafs)
    throws IOException
  {
    this.files = rafs;
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


  /**
   * Creates a new <code>FileWordList</code> with the supplied files and lower
   * case property. The files are immediately read in order to store the
   * position of every word in each file.
   *
   * @param  rafs  <code>RandomAccessFile[]</code> to read
   * @param  lc  <code>boolean</code> whether to lower case when reading
   * @throws  IOException  if an error occurs reading the supplied file
   */
  public FilePointerWordList(final RandomAccessFile[] rafs, final boolean lc)
    throws IOException
  {
    this(rafs);
    this.setLowerCase(lc);
  }


  /**
   * Creates a new <code>FileWordList</code> with the supplied file, list of
   * line positions, and lower case parameters.
   *
   * @param  rafs  <code>RandomAccessFile[]</code> to reference
   * @param  data  <code>List</code> of line positions
   * @param  lc  <code>boolean</code> whether to lower case when reading
   */
  private FilePointerWordList(
    final RandomAccessFile[] rafs,
    final List<WordReference> data,
    final boolean lc)
  {
    this.files = rafs;
    this.references = new ArrayList<WordReference>(
      data.size());
    this.references.addAll(data);
    this.lowerCase = lc;
  }


  /** {@inheritDoc} */
  public boolean add(final String s)
  {
    this.checkNull(s);
    final WordReference wr = this.getWordReference(s);
    if (wr == null) {
      throw new IllegalArgumentException(
        "Supplied string (" + s + ") must exist in the files");
    }
    return this.references.add(wr);
  }


  /** {@inheritDoc} */
  public void add(final int index, final String s)
  {
    this.checkRange(index);
    this.checkNull(s);
    final WordReference wr = this.getWordReference(s);
    if (wr == null) {
      throw new IllegalArgumentException(
        "Supplied string (" + s + ") must exist in the files");
    }
    this.references.add(index, wr);
  }


  /** {@inheritDoc} */
  public void clear()
  {
    this.references.clear();
  }


  /** {@inheritDoc} */
  public String get(final int index)
  {
    this.checkRange(index);
    return this.getWord(this.references.get(index));
  }


  /** {@inheritDoc} */
  public int indexOf(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    final WordReference wr = this.getWordReference((String) o);
    return wr == null ? -1 : this.references.indexOf(wr);
  }


  /** {@inheritDoc} */
  public Iterator<String> iterator()
  {
    return new FilePointerWordIterator(this.references.iterator());
  }


  /** {@inheritDoc} */
  public int lastIndexOf(final Object o)
  {
    this.checkNull(o);
    this.checkIsString(o);
    final WordReference wr = this.getWordReference((String) o);
    return wr == null ? -1 : this.references.lastIndexOf(wr);
  }


  /** {@inheritDoc} */
  public ListIterator<String> listIterator()
  {
    return new FilePointerWordListIterator(this.references.listIterator());
  }


  /** {@inheritDoc} */
  public ListIterator<String> listIterator(final int index)
  {
    return new FilePointerWordListIterator(this.references.listIterator(index));
  }


  /** {@inheritDoc} */
  public String remove(final int index)
  {
    this.checkRange(index);
    return this.getWord(this.references.remove(index));
  }


  /** {@inheritDoc} */
  public boolean remove(final Object o)
  {
    final int index = this.indexOf(o);
    if (index == -1) {
      return false;
    }
    return this.references.remove(index) != null;
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
    boolean modified = false;
    final Iterator<WordReference> i = this.references.iterator();
    while (i.hasNext()) {
      final String word = this.getWord(i.next());
      if (!c.contains(word)) {
        i.remove();
        modified = true;
      }
    }
    return modified;
  }


  /** {@inheritDoc} */
  public String set(final int index, final String s)
  {
    this.checkRange(index);
    final WordReference wr = this.getWordReference(s);
    if (wr == null) {
      throw new IllegalArgumentException(
        "Supplied string (" + s + ") must exist in the files");
    }
    final String word = this.get(index);
    this.references.set(index, wr);
    return word;
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.references.size();
  }


  /** {@inheritDoc} */
  public FilePointerWordList subList(final int fromIndex, final int toIndex)
  {
    return new FilePointerWordList(
      this.files,
      this.references.subList(fromIndex, toIndex),
      this.lowerCase);
  }


  /**
   * Returns the first word reference that contains the supplied word. See
   * {@link #readFile(RandomAccessFile, String)}. Returns null if the word
   * cannot be found.
   *
   * @param  word  <code>String</code> to find
   * @return  <code>WordReference</code> containing the supplied word
   */
  protected WordReference getWordReference(final String word)
  {
    RandomAccessFile file = null;
    long pos = -1;
    for (RandomAccessFile raf : this.files) {
      pos = this.readFile(raf, word);
      if (pos >= 0) {
        file = raf;
        break;
      }
    }
    if (pos == -1) {
      return null;
    } else {
      return new WordReference(file, pos);
    }
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
        return lowerCase ? raf.readLine().toLowerCase() : raf.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file", e);
    }
  }


  /**
   * Searches the file line by line and returns the position of the supplied
   * word. Note that this is an expensive operation which reads every line of
   * the supplied file, starting at the beginning. Returns -1 if the word
   * cannot be found.
   *
   * @param  raf  <code>RandomAccessFile</code> to read
   * @param  word  <code>String</code> to search for
   * @return  <code>long</code> position of the supplied word in the file
   * @throws  IllegalStateException if an error occurs reading the file
   */
  private long readFile(final RandomAccessFile raf, final String word)
  {
    String s;
    try {
      synchronized (raf) {
        long pos = 0L;
        raf.seek(pos);
        while ((s = raf.readLine()) != null) {
          if (this.lowerCase) {
            s = s.toLowerCase();
          }
          if (s.equals(word)) {
            return pos;
          } else {
            pos = raf.getFilePointer();
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file", e);
    }
    return -1;
  }


  /**
   * Iterator implementation for this word list. This implementation delegates
   * iterator operations to the underlying list of word references.
   */
  private class FilePointerWordIterator implements Iterator<String>
  {

    /** iterator of line positions. */
    protected Iterator<WordReference> lpIterator;


    /**
     * Creates a new <code>FileWordIterator</code> with the supplied iterator.
     *
     * @param  i  <code>Iterator</code> of line pointers
     */
    public FilePointerWordIterator(
      final Iterator<WordReference> i)
    {
      this.lpIterator = i;
    }


    /** {@inheritDoc} */
    public boolean hasNext()
    {
      return this.lpIterator.hasNext();
    }


    /** {@inheritDoc} */
    public String next()
    {
      return FilePointerWordList.this.getWord(this.lpIterator.next());
    }


    /** {@inheritDoc} */
    public void remove()
    {
      this.lpIterator.remove();
    }
  }


  /**
   * ListIterator implementation for this word list. This implementation
   * delegates list iterator operations to the underlying list of word
   * references.
   */
  private class FilePointerWordListIterator extends FilePointerWordIterator
    implements ListIterator<String>
  {

    /** iterator of line positions. */
    protected ListIterator<WordReference> lpIterator;


    /**
     * Creates a new <code>WordListIterator</code> with the supplied
     * iterator.
     *
     * @param  i  <code>ListIterator</code> of line pointers
     */
    public FilePointerWordListIterator(final ListIterator<WordReference> i)
    {
      super(i);
      this.lpIterator = i;
    }


    /** {@inheritDoc} */
    public boolean hasPrevious()
    {
      return this.lpIterator.hasPrevious();
    }


    /** {@inheritDoc} */
    public int nextIndex()
    {
      return this.lpIterator.nextIndex();
    }


    /** {@inheritDoc} */
    public String previous()
    {
      return FilePointerWordList.this.getWord(this.lpIterator.previous());
    }


    /** {@inheritDoc} */
    public int previousIndex()
    {
      return this.lpIterator.previousIndex();
    }


    /** {@inheritDoc} */
    public void set(final String s)
    {
      final WordReference wr = FilePointerWordList.this.getWordReference(s);
      if (wr == null) {
        throw new IllegalArgumentException(
          "Supplied string (" + s + ") must exist in the files");
      }
      this.lpIterator.set(wr);
    }


    /** {@inheritDoc} */
    public void add(final String s)
    {
      final WordReference wr = FilePointerWordList.this.getWordReference(s);
      if (wr == null) {
        throw new IllegalArgumentException(
          "Supplied string (" + s + ") must exist in the files");
      }
      this.lpIterator.add(wr);
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
