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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <code>StringListDictionary</code> provides fast searching for dictionary
 * words using a {@link java.util.List}.  The entire dictionary is stored in
 * memory, so heap size may need to be adjusted to accommodate large
 * dictionaries. Search method delegates to
 * {@link java.util.Collections#binarySearch(List, Object)}. It's critical that
 * the word list provided to this dictionary be sorted according to the natural
 * ordering of {@link java.lang.String}. This class inherits the lower case
 * property of the supplied word list.
 *
 * @author  Middleware Services
 * @version  $Revision: 1252 $ $Date: 2010-04-16 17:24:23 -0400 (Fri, 16 Apr 2010) $
 */
public class StringListDictionary implements Dictionary
{

  /** list used for searching. */
  protected List<String> list;

  /** to insert into the list. */
  protected MutableWordList wordList;

  /** whether search terms should be lowercased. Default value is {@value}. */
  protected boolean lowerCase;


  /**
   * Sets the word list to insert into this dictionary. The supplied word list
   * will be emptied by a call to {@link #initialize()}.
   *
   * @param  wl  <code>MutableWordList</code> to read from
   */
  public void setWordList(final MutableWordList wl)
  {
    this.wordList = wl;
  }


  /** {@inheritDoc} */
  public void initialize()
    throws IOException
  {
    this.list = new ArrayList<String>(this.wordList.size());
    while (this.wordList.size() > 0) {
      this.list.add(this.wordList.remove(0));
    }
    this.lowerCase = this.wordList.isLowerCase();
    this.wordList.close();
  }


  /** {@inheritDoc} */
  public boolean search(final String word)
  {
    if (this.lowerCase) {
      return Collections.binarySearch(this.list, word.toLowerCase()) >= 0;
    } else {
      return Collections.binarySearch(this.list, word) >= 0;
    }
  }


  /**
   * Returns whether the supplied word exists in the dictionary. See
   * {@link java.util.Collections#binarySearch(List, Object, Comparator)}.
   *
   * @param  word  <code>String</code> to search for
   * @param  c  <code>Comparator</code> to use against the word list
   *
   * @return  <code>boolean</code> - whether word was found
   */
  public boolean search(final String word, final Comparator<String> c)
  {
    if (this.lowerCase) {
      return Collections.binarySearch(this.list, word.toLowerCase(), c) >= 0;
    } else {
      return Collections.binarySearch(this.list, word, c) >= 0;
    }
  }


  /**
   * This will the list of all the words in this dictionary. The returned list
   * cannot be modified.
   *
   * @return  <code>List</code> - of words
   */
  public List<String> getStringList()
  {
    return Collections.unmodifiableList(this.list);
  }


  /** {@inheritDoc} */
  public void close()
    throws IOException
  {
    this.wordList.close();
  }


  /**
   * This provides command line access to this
   * <code>StringListDictionary</code>.
   *
   * @param  args  <code>String[]</code>
   *
   * @throws  Exception  if an error occurs
   */
  public static void main(final String[] args)
    throws Exception
  {
    final List<RandomAccessFile> files = new ArrayList<RandomAccessFile>();
    try {
      if (args.length == 0) {
        throw new ArrayIndexOutOfBoundsException();
      }

      // dictionary operations
      boolean ignoreCase = false;
      boolean search = false;
      boolean print = false;

      // operation parameters
      String word = null;

      for (int i = 0; i < args.length; i++) {
        if ("-ci".equals(args[i])) {
          ignoreCase = true;
        } else if ("-s".equals(args[i])) {
          search = true;
          word = args[++i];
        } else if ("-p".equals(args[i])) {
          print = true;
        } else if ("-h".equals(args[i])) {
          throw new ArrayIndexOutOfBoundsException();
        } else {
          files.add(new RandomAccessFile(args[i], "r"));
        }
      }

      // insert data
      final StringListDictionary dict = new StringListDictionary();
      dict.setWordList(
        new FilePointerWordList(
          files.toArray(new RandomAccessFile[files.size()]), ignoreCase));
      dict.initialize();

      // perform operation
      if (search) {
        if (dict.search(word)) {
          System.out.println(
            String.format("%s was found in this dictionary", word));
        } else {
          System.out.println(
            String.format("%s was not found in this dictionary", word));
        }
      } else if (print) {
        System.out.println(dict.getStringList());
      } else {
        throw new ArrayIndexOutOfBoundsException();
      }

    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: java " +
        StringListDictionary.class.getName() + " \\");
      System.out.println(
        "       <dictionary1> <dictionary2> ... " +
        "<options> <operation> \\");
      System.out.println("");
      System.out.println("where <options> includes:");
      System.out.println("       -ci (Make search case-insensitive) \\");
      System.out.println("");
      System.out.println("where <operation> includes:");
      System.out.println("       -s <word> (Search for a word) \\");
      System.out.println("       -p (Print the entire dictionary) \\");
      System.out.println("       -h (Print this message) \\");
      System.exit(1);
    }
  }
}
