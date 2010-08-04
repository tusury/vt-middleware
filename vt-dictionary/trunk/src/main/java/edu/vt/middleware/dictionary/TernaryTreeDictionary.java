/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.dictionary;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <code>TernaryTreeDictionary</code> provides fast searching for dictionary
 * words using a ternary tree. The entire dictionary is stored in memory, so
 * heap size may need to be adjusted to accommodate large dictionaries. It is
 * highly recommended that sorted word lists be inserted using their median.
 * This helps to produce a balanced ternary tree which improves search time.
 * This class inherits the lower case property of the supplied word list.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class TernaryTreeDictionary implements Dictionary
{

  /** ternary tree used for searching. */
  protected TernaryTree tree = new TernaryTree();

  /** to build the ternary tree from. */
  protected MutableWordList wordList;

  /** whether search terms should be lower cased. Default value is {@value}. */
  protected boolean lowerCase;

  /** whether to read from the median of the word list.
      Default value is {@value}. */
  protected boolean useMedian = true;


  /**
   * Sets the word list to build the ternary tree. The supplied word list
   * will be emptied by a call to {@link #initialize()}.
   *
   * @param  wl  <code>MutableWordList</code> to read from
   */
  public void setWordList(final MutableWordList wl)
  {
    this.wordList = wl;
  }


  /**
   * Whether the word list should be read from it's median rather than it's
   * beginning.
   *
   * @return  <code>boolean</code>
   */
  public boolean isUseMedian()
  {
    return this.useMedian;
  }


  /**
   * Sets whether the word list should be read from it's median rather than it's
   * beginning.
   *
   * @param  b  <code>boolean</code>
   */
  public void setUseMedian(final boolean b)
  {
    this.useMedian = b;
  }


  /** {@inheritDoc} */
  public void initialize()
    throws IOException
  {
    while (this.wordList.size() > 0) {
      final int i =
        this.useMedian ? (int) Math.floor(this.wordList.size() / 2) : 0;
      this.tree.insert(this.wordList.remove(i));
    }
    this.lowerCase = this.wordList.isLowerCase();
    this.wordList.close();
  }


  /** {@inheritDoc} */
  public boolean search(final String word)
  {
    if (this.lowerCase) {
      return this.tree.search(word.toLowerCase());
    } else {
      return this.tree.search(word);
    }
  }


  /**
   * This will return an array of strings which partially match the supplied
   * word. This search is case sensitive by default.
   * See {@link TernaryTree#partialSearch}.
   *
   * @param  word  <code>String</code> to search for
   *
   * @return  <code>String[]</code> - of matching words
   */
  public String[] partialSearch(final String word)
  {
    if (this.lowerCase) {
      return this.tree.partialSearch(word.toLowerCase());
    } else {
      return this.tree.partialSearch(word);
    }
  }


  /**
   * This will return an array of strings which are near to the supplied word by
   * the supplied distance. This search is case sensitive by default.
   * See {@link TernaryTree#nearSearch}.
   *
   * @param  word  <code>String</code> to search for
   * @param  distance  <code>int</code> for valid match
   *
   * @return  <code>String[]</code> - of matching words
   */
  public String[] nearSearch(final String word, final int distance)
  {
    if (this.lowerCase) {
      return this.tree.nearSearch(word.toLowerCase(), distance);
    } else {
      return this.tree.nearSearch(word, distance);
    }
  }


  /**
   * Returns the underlying ternary tree used by this dictionary.
   *
   * @return  <code>TernaryTree</code>
   */
  public TernaryTree getTernaryTree()
  {
    return this.tree;
  }


  /** {@inheritDoc} */
  public void close()
    throws IOException
  {
    this.wordList.close();
    this.tree = null;
  }


  /**
   * This provides command line access to a <code>TernaryTreeDictionary</code>.
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
      boolean useMedian = false;
      boolean ignoreCase = false;
      boolean search = false;
      boolean partialSearch = false;
      boolean nearSearch = false;
      boolean print = false;

      // operation parameters
      String word = null;
      int distance = 0;

      for (int i = 0; i < args.length; i++) {
        if ("-m".equals(args[i])) {
          useMedian = true;
        } else if ("-ci".equals(args[i])) {
          ignoreCase = true;
        } else if ("-s".equals(args[i])) {
          search = true;
          word = args[++i];
        } else if ("-ps".equals(args[i])) {
          partialSearch = true;
          word = args[++i];
        } else if ("-ns".equals(args[i])) {
          nearSearch = true;
          word = args[++i];
          distance = Integer.parseInt(args[++i]);
        } else if ("-p".equals(args[i])) {
          print = true;
        } else if ("-h".equals(args[i])) {
          throw new ArrayIndexOutOfBoundsException();
        } else {
          files.add(new RandomAccessFile(args[i], "r"));
        }
      }

      // insert data
      final TernaryTreeDictionary dict = new TernaryTreeDictionary();
      dict.setUseMedian(useMedian);
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
      } else if (partialSearch) {
        final String[] matches = dict.partialSearch(word);
        System.out.println(
          String.format(
            "Found %s matches for %s in this dictionary : %s",
            matches.length, word, Arrays.asList(matches)));
      } else if (nearSearch) {
        final String[] matches = dict.nearSearch(word, distance);
        System.out.println(
          String.format(
            "Found %s matches for %s in this dictionary at a distance of %s " +
            ": %s", matches.length, word, distance, Arrays.asList(matches)));
      } else if (print) {
        dict.getTernaryTree().print(new PrintWriter(System.out, true));
      } else {
        throw new ArrayIndexOutOfBoundsException();
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: java " +
        TernaryTreeDictionary.class.getName() + " \\");
      System.out.println(
        "       <dictionary1> <dictionary2> ... " +
        "<options> <operation> \\");
      System.out.println("");
      System.out.println("where <options> includes:");
      System.out.println("       -m (Insert dictionary using it's median) \\");
      System.out.println("       -ci (Make search case-insensitive) \\");
      System.out.println("");
      System.out.println("where <operation> includes:");
      System.out.println("       -s <word> (Search for a word) \\");
      System.out.println("       -ps <word> (Partial search for a word) \\");
      System.out.println("           (where word like '.a.a.a') \\");
      System.out.println(
        "       -ns <word> <distance> " +
        "(Near search for a word) \\");
      System.out.println(
        "       -p (Print the entire dictionary " + "in tree form) \\");
      System.out.println("       -h (Print this message) \\");
      System.exit(1);
    }
  }
}
