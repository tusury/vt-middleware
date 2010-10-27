/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.dictionary;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>TernaryTree</code> is an implementation of a ternary tree. Methods are
 * provided for inserting strings and searching for strings. The algorithms in
 * this class are all recursive, and have not been optimized for any particular
 * purpose. Data which is inserted is not sorted before insertion, however data
 * can be inserted beginning with the median of the supplied data.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class TernaryTree
{

  /** Case sensitive comparator. */
  protected static final Comparator<Character> CASE_SENSITIVE_COMPARATOR =
    new Comparator<Character>() {
      public int compare(final Character a, final Character b)
      {
        int result = 0;
        final char c1 = a.charValue();
        final char c2 = b.charValue();
        if (c1 < c2) {
          result = -1;
        } else if (c1 > c2) {
          result = 1;
        }
        return result;
      }
    };

  /** Case insensitive comparator. */
  protected static final Comparator<Character> CASE_INSENSITIVE_COMPARATOR =
    new Comparator<Character>() {
      public int compare(final Character a, final Character b)
      {
        int result = 0;
        final char c1 = Character.toLowerCase(a.charValue());
        final char c2 = Character.toLowerCase(b.charValue());
        if (c1 < c2) {
          result = -1;
        } else if (c1 > c2) {
          result = 1;
        }
        return result;
      }
    };

  /** File system line separator. */
  private static final String LINE_SEPARATOR = System.getProperty(
    "line.separator");

  /** Character comparator. */
  protected Comparator<Character> comparator;

  /** root node of the ternary tree. */
  private TernaryNode root;


  /** Creates an empty case sensitive ternary tree. */
  public TernaryTree()
  {
    this(true);
  }


  /**
   * Creates an empty ternary tree with the given case sensitivity.
   *
   * @param  caseSensitive  True to create case-sensitive tree, false otherwise.
   */
  public TernaryTree(final boolean caseSensitive)
  {
    if (caseSensitive) {
      this.comparator = CASE_SENSITIVE_COMPARATOR;
    } else {
      this.comparator = CASE_INSENSITIVE_COMPARATOR;
    }
  }


  /**
   * This will insert the supplied word into the <code>TernaryTree</code>.
   *
   * @param  word  <code>String</code> to insert
   */
  public void insert(final String word)
  {
    if (word != null) {
      this.root = insertNode(this.root, word, 0);
    }
  }


  /**
   * This will insert the supplied array of words into the <code>
   * TernaryTree</code>.
   *
   * @param  words  <code>String[]</code> to insert
   */
  public void insert(final String[] words)
  {
    if (words != null) {
      for (String s : words) {
        this.insert(s);
      }
    }
  }


  /**
   * This will return true if the supplied word has been inserted into the
   * <code>TernaryTree</code>.
   *
   * @param  word  <code>String</code> to search for
   *
   * @return  <code>boolean</code> - whether word was found
   */
  public boolean search(final String word)
  {
    return this.searchNode(this.root, word, 0);
  }


  /**
   * This will return an array of strings which partially match the supplied
   * word. word should be of the format '.e.e.e' Where the '.' character
   * represents any valid character. Possible results from this query include:
   * Helene, delete, or severe Note that no substring matching occurs, results
   * only include strings of the same length. If the supplied word does not
   * contain the '.' character, then a regular search is performed.
   *
   * <p><strong>NOTE</strong> This method is not supported for case insensitive
   * ternary trees. Since the tree is built without regard to case any words
   * returned from the tree may or may not match the case of the supplied word.
   * </p>
   *
   * @param  word  <code>String</code> to search for
   *
   * @return  <code>String[]</code> - of matching words
   *
   * @throws  UnsupportedOperationException  if this is a case insensitive
   * ternary tree
   */
  public String[] partialSearch(final String word)
  {
    if (this.comparator == CASE_INSENSITIVE_COMPARATOR) {
      throw new UnsupportedOperationException(
        "Partial search is not supported for case insensitive ternary trees");
    }

    String[] results = null;
    final List<String> matches = this.partialSearchNode(
      this.root,
      new ArrayList<String>(),
      "",
      word,
      0);
    if (matches == null) {
      results = new String[] {};
    } else {
      results = matches.toArray(new String[matches.size()]);
    }
    return results;
  }


  /**
   * This will return an array of strings which are near to the supplied word by
   * the supplied distance. For the query nearSearch("fisher", 2): Possible
   * results include: cipher, either, fishery, kosher, sister. If the supplied
   * distance is not > 0, then a regular search is performed.
   *
   * <p><strong>NOTE</strong> This method is not supported for case insensitive
   * ternary trees. Since the tree is built without regard to case any words
   * returned from the tree may or may not match the case of the supplied word.
   * </p>
   *
   * @param  word  <code>String</code> to search for
   * @param  distance  <code>int</code> for valid match
   *
   * @return  <code>String[]</code> - of matching words
   *
   * @throws  UnsupportedOperationException  if this is a case insensitive
   * ternary tree
   */
  public String[] nearSearch(final String word, final int distance)
  {
    if (this.comparator == CASE_INSENSITIVE_COMPARATOR) {
      throw new UnsupportedOperationException(
        "Near search is not supported for case insensitive ternary trees");
    }

    String[] results = null;
    final List<String> matches = this.nearSearchNode(
      this.root,
      distance,
      new ArrayList<String>(),
      "",
      word,
      0);
    if (matches == null) {
      results = new String[] {};
    } else {
      results = matches.toArray(new String[matches.size()]);
    }
    return results;
  }


  /**
   * This will return a list of all the words in this <code>TernaryTree</code>.
   * This is a very expensive operation, every node in the tree is traversed.
   * The returned list cannot be modified.
   *
   * @return  <code>String[]</code> - of words
   */
  public List<String> getWords()
  {
    final List<String> words = this.traverseNode(
      this.root,
      "",
      new ArrayList<String>());
    return Collections.unmodifiableList(words);
  }


  /**
   * This will print an ASCII representation of this <code>TernaryTree</code> to
   * the supplied <code>PrintWriter</code>. This is a very expensive operation,
   * every node in the tree is traversed. The output produced is hard to read,
   * but it should give an indication of whether or not your tree is balanced.
   *
   * @param  out  <code>PrintWriter</code> to print to
   *
   * @throws  IOException  if an error occurs
   */
  public void print(final Writer out)
    throws IOException
  {
    out.write(printNode(this.root, "", 0));
  }


  /**
   * This will recursively insert a word into the <code>TernaryTree</code> one
   * node at a time beginning at the supplied node.
   *
   * @param  node  <code>TernaryNode</code> to put character in
   * @param  word  <code>String</code> to be inserted
   * @param  index  <code>int</code> of character in word
   *
   * @return  <code>TernaryNode</code> - to insert
   */
  private TernaryNode insertNode(
    TernaryNode node,
    final String word,
    final int index)
  {
    if (index < word.length()) {
      final char c = word.charAt(index);
      if (node == null) {
        node = new TernaryNode(c);
      }

      final char split = node.getSplitChar();
      final int cmp = this.comparator.compare(c, split);
      if (cmp < 0) {
        node.setLokid(insertNode(node.getLokid(), word, index));
      } else if (cmp == 0) {
        if (index == word.length() - 1) {
          node.setEndOfWord(true);
        }
        node.setEqkid(insertNode(node.getEqkid(), word, index + 1));
      } else {
        node.setHikid(insertNode(node.getHikid(), word, index));
      }
    }
    return node;
  }


  /**
   * This will recursively search for a word in the <code>TernaryTree</code> one
   * node at a time beginning at the supplied node.
   *
   * @param  node  <code>TernaryNode</code> to search in
   * @param  word  <code>String</code> to search for
   * @param  index  <code>int</code> of character in word
   *
   * @return  <code>boolean</code> - whether or not word was found
   */
  private boolean searchNode(
    final TernaryNode node,
    final String word,
    final int index)
  {
    boolean success = false;
    if (node != null && index < word.length()) {
      final char c = word.charAt(index);
      final char split = node.getSplitChar();
      final int cmp = this.comparator.compare(c, split);
      if (cmp < 0) {
        return searchNode(node.getLokid(), word, index);
      } else if (cmp > 0) {
        return searchNode(node.getHikid(), word, index);
      } else {
        if (index == word.length() - 1) {
          if (node.isEndOfWord()) {
            success = true;
          }
        } else {
          return searchNode(node.getEqkid(), word, index + 1);
        }
      }
    }
    return success;
  }


  /**
   * This will recursively search for a partial word in the <code>
   * TernaryTree</code> one node at a time beginning at the supplied node.
   *
   * @param  node  <code>TernaryNode</code> to search in
   * @param  matches  <code>ArrayList</code> of partial matches
   * @param  match  <code>String</code> the current word being examined
   * @param  word  <code>String</code> to search for
   * @param  index  <code>int</code> of character in word
   *
   * @return  <code>ArrayList</code> - of matches
   */
  private List<String> partialSearchNode(
    final TernaryNode node,
    List<String> matches,
    final String match,
    final String word,
    final int index)
  {
    if (node != null && index < word.length()) {
      final char c = word.charAt(index);
      final char split = node.getSplitChar();
      final int cmp = this.comparator.compare(c, split);
      if (c == '.' || cmp < 0) {
        matches = partialSearchNode(
          node.getLokid(),
          matches,
          match,
          word,
          index);
      }
      if (c == '.' || cmp == 0) {
        if (index == word.length() - 1) {
          if (node.isEndOfWord()) {
            matches.add(match + split);
          }
        } else {
          matches = partialSearchNode(
            node.getEqkid(),
            matches,
            match + split,
            word,
            index + 1);
        }
      }
      if (c == '.' || cmp > 0) {
        matches = partialSearchNode(
          node.getHikid(),
          matches,
          match,
          word,
          index);
      }
    }
    return matches;
  }


  /**
   * This will recursively search for a near match word in the <code>
   * TernaryTree</code> one node at a time beginning at the supplied node.
   *
   * @param  node  <code>TernaryNode</code> to search in
   * @param  distance  <code>int</code> of a valid match, must be > 0
   * @param  matches  <code>ArrayList</code> of near matches
   * @param  match  <code>String</code> the current word being examined
   * @param  word  <code>String</code> to search for
   * @param  index  <code>int</code> of character in word
   *
   * @return  <code>ArrayList</code> - of matches
   */
  private List<String> nearSearchNode(
    final TernaryNode node,
    final int distance,
    List<String> matches,
    final String match,
    final String word,
    final int index)
  {
    if (node != null && distance >= 0) {

      final char c;
      if (index < word.length()) {
        c = word.charAt(index);
      } else {
        c = (char) -1;
      }

      final char split = node.getSplitChar();
      final int cmp = this.comparator.compare(c, split);

      if (distance > 0 || cmp < 0) {
        matches = nearSearchNode(
          node.getLokid(),
          distance,
          matches,
          match,
          word,
          index);
      }

      final String newMatch = match + split;
      if (cmp == 0) {

        if (
          node.isEndOfWord() &&
            distance >= 0 &&
            newMatch.length() + distance >= word.length()) {
          matches.add(newMatch);
        }

        matches = nearSearchNode(
          node.getEqkid(),
          distance,
          matches,
          newMatch,
          word,
          index + 1);
      } else {

        if (
          node.isEndOfWord() &&
            distance - 1 >= 0 &&
            newMatch.length() + distance - 1 >= word.length()) {
          matches.add(newMatch);
        }

        matches = nearSearchNode(
          node.getEqkid(),
          distance - 1,
          matches,
          newMatch,
          word,
          index + 1);
      }

      if (distance > 0 || cmp > 0) {
        matches = nearSearchNode(
          node.getHikid(),
          distance,
          matches,
          match,
          word,
          index);
      }
    }
    return matches;
  }


  /**
   * This will recursively traverse every node in the <code>TernaryTree</code>
   * one node at a time beginning at the supplied node. The result is a string
   * representing every word, which is delimited by the LINE_SEPARATOR
   * character.
   *
   * @param  node  <code>TernaryNode</code> to begin traversing
   * @param  s  <code>String</code> of words found at the supplied node
   * @param  words  <code>ArrayList</code> which will be returned (recursive
   * function)
   *
   * @return  <code>String</code> - containing all words from the supplied node
   */
  private List<String> traverseNode(
    final TernaryNode node,
    final String s,
    List<String> words)
  {
    if (node != null) {

      words = this.traverseNode(node.getLokid(), s, words);

      final String c = String.valueOf(node.getSplitChar());
      if (node.getEqkid() != null) {
        words = this.traverseNode(node.getEqkid(), s + c, words);
      }

      if (node.isEndOfWord()) {
        words.add(s + c);
      }

      words = this.traverseNode(node.getHikid(), s, words);
    }
    return words;
  }


  /**
   * This will recursively traverse every node in the <code>TernaryTree</code>
   * one node at a time beginning at the supplied node. The result is an ASCII
   * string representation of the tree beginning at the supplied node.
   *
   * @param  node  <code>TernaryNode</code> to begin traversing
   * @param  s  <code>String</code> of words found at the supplied node
   * @param  depth  <code>int</code> of the current node
   *
   * @return  <code>String</code> - containing all words from the supplied node
   */
  private String printNode(
    final TernaryNode node,
    final String s,
    final int depth)
  {
    final StringBuffer buffer = new StringBuffer();
    if (node != null) {
      buffer.append(this.printNode(node.getLokid(), " <-", depth + 1));

      final String c = String.valueOf(node.getSplitChar());
      final StringBuffer eq = new StringBuffer();
      if (node.getEqkid() != null) {
        eq.append(this.printNode(node.getEqkid(), s + c + "--", depth + 1));
      } else {
        int count = (new StringTokenizer(s, "--")).countTokens();
        if (count > 0) {
          count--;
        }
        for (int i = 1; i < depth - count - 1; i++) {
          eq.append("   ");
        }
        eq.append(s).append(c).append(TernaryTree.LINE_SEPARATOR);
      }
      buffer.append(eq);

      buffer.append(this.printNode(node.getHikid(), " >-", depth + 1));
    }
    return buffer.toString();
  }
}
