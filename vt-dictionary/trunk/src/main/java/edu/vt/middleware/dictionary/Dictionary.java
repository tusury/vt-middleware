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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>Dictionary</code> is a class which manages a <code>TernaryTree</code>.
 * Methods are provided for inserting data, sorting, and searching. Example:
 *
 * <pre>
   import edu.vt.middleware.dictionary.Dictionary;

   String searchTerm = "my_search_term";
   Dictionary d = new Dictionary();
   d.useMedian();
   d.ignoreCase();
   d.insert(new java.io.File("my.dictionary"));
   d.build();
   if (dict.search(searchTerm)) {
     System.out.println(searchTerm+" was found in this dictionary");
   } else {
     System.out.println(searchTerm+" was not found in this dictionary");
   }
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class Dictionary
{

  /** ternary tree */
  private TernaryTree tree = new TernaryTree();

  /** store all words in the dictionary */
  private List<String> words = new ArrayList<String>();

  /** whether to insert data using the median of that data */
  private boolean median;

  /** whether to ignore case when inserting data */
  private boolean ignoreCase;


  /** Default Constructor. */
  public Dictionary() {}


  /**
   * This will insert the supplied word into the <code>Dictionary</code>. Empty
   * lines are ignored. Lines beginning with '#' are ignored.
   *
   * @param  newWord  <code>String</code> to insert
   */
  public void insert(final String newWord)
  {
    if (newWord != null) {
      newWord.trim();

      final StringTokenizer st = new StringTokenizer(newWord);
      while (st.hasMoreTokens()) {
        final String line = st.nextToken();
        if (!line.startsWith("#")) {
          if (this.ignoreCase) {
            this.words.add(line.toLowerCase());
          } else {
            this.words.add(line);
          }
        }
      }
    }
  }


  /**
   * This will insert the supplied array of words into the <code>
   * Dictionary</code>. See {@link #insert(String)}.
   *
   * @param  newWords  <code>String[]</code> to insert
   */
  public void insert(final String[] newWords)
  {
    if (newWords != null) {
      for (String s : newWords) {
        this.insert(s);
      }
    }
  }


  /**
   * This will insert the words in the supplied file into the <code>
   * Dictionary</code>. The file should contain one word per line or multiple
   * words per line (Separated by whitespace). See {@link #insert(String)}.
   *
   * @param  file  <code>File</code> of words to insert
   *
   * @throws  IOException  if an error occurs while reading from file
   */
  public void insert(final File file)
    throws IOException
  {
    final BufferedReader in = new BufferedReader(new FileReader(file));
    String line;
    while ((line = in.readLine()) != null) {
      this.insert(line);
    }
  }


  /**
   * This will insert the words in the supplied inputstream into the <code>
   * Dictionary</code>. The inputstream should contain one word per line or
   * multiple words per line (Separated by whitespace). See {@link
   * #insert(String)}.
   *
   * @param  is  <code>InputStream</code> of words to insert
   *
   * @throws  IOException  if an error occurs while reading from the inputstream
   */
  public void insert(final InputStream is)
    throws IOException
  {
    final BufferedReader in = new BufferedReader(new InputStreamReader(is));
    String line;
    while ((line = in.readLine()) != null) {
      this.insert(line);
    }
  }


  /**
   * This will sort the words in this <code>Dictionary</code> using the bubble
   * sort algorithm. This method must be called before {@link #build()}
   */
  public void bubbleSort()
  {
    final int n = this.words.size();
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - 1 - i; j++) {
        final String a = this.words.get(j);
        final String b = this.words.get(j + 1);
        if (a.compareTo(b) > 0) {
          this.words.set(j, b);
          this.words.set(j + 1, a);
        }
      }
    }
  }


  /**
   * This will sort the words in this <code>Dictionary</code> using the
   * selection sort algorithm. This method must be called before {@link
   * #build()}
   */
  public void selectionSort()
  {
    final int n = this.words.size();
    for (int i = 0; i < n - 1; i++) {
      int min = i;
      for (int j = i + 1; j < n; j++) {
        final String b = this.words.get(j);
        if (b.compareTo(this.words.get(min)) < 0) {
          min = j;
        }
      }
      this.words.set(i, this.words.set(min, this.words.get(i)));
    }
  }


  /**
   * This will sort the words in this <code>Dictionary</code> using the
   * insertion sort algorithm. This method must be called before {@link
   * #build()}
   */
  public void insertionSort()
  {
    final int n = this.words.size();
    for (int i = 1; i < n; i++) {
      int j = i - 1;
      final String a = this.words.get(i);
      String b = this.words.get(j);
      while (j >= 0 && (a.compareTo(b = this.words.get(j)) < 0)) {
        this.words.set(j + 1, b);
        j--;
      }
      this.words.set(j + 1, a);
    }
  }


  /**
   * This will sort the words in this <code>Dictionary</code> using the quick
   * sort algorithm. This method must be called before {@link #build()}
   */
  public void quickSort()
  {
    if (this.words.size() > 0) {
      this.quickSort(0, this.words.size() - 1);
    }
  }


  /**
   * This will sort the words in this <code>Dictionary</code> beginning at the
   * lo index and ending at the hi index, using the quick sort algorithm. This
   * method must be called before {@link #build()}
   *
   * @param  lo  <code>int</code> index to beginning sorting at
   * @param  hi  <code>int</code> index to stop sorting at
   */
  public void quickSort(final int lo, final int hi)
  {
    final int m = (int) Math.floor((lo + hi) / 2);
    final String x = this.words.get(m);

    int i = lo;
    int j = hi;
    do {
      while (x.compareTo(this.words.get(i)) > 0) {
        i++;
      }

      while (x.compareTo(this.words.get(j)) < 0) {
        j--;
      }

      if (i <= j) {
        this.words.set(j, this.words.set(i, this.words.get(j)));
        i++;
        j--;
      }
    } while (i <= j);

    if (lo < j) {
      this.quickSort(lo, j);
    }
    if (i < hi) {
      this.quickSort(i, hi);
    }
  }


  /**
   * This will build a ternary tree using any words that have been inserted into
   * the <code>Dictionary</code>. This method must be called before any
   * searching can be preformed. If {@link #useMedian()} has been called, then
   * data will be inserted beginning from the median.
   */
  public void build()
  {
    final String[] w = this.words.toArray(new String[0]);
    if (this.median) {
      final int m = (int) Math.floor(w.length / 2);
      this.tree.insert(w[m]);
      for (int i = 1; i <= m; i++) {
        this.tree.insert(w[m - i]);
        if (m + i < w.length) {
          this.tree.insert(w[m + i]);
        }
      }
    } else {
      for (String s : w) {
        this.tree.insert(s);
      }
    }
  }


  /**
   * This will return true if the supplied word has been inserted into the
   * <code>Dictionary</code>. This search is case sensitive. See {@link
   * TernaryTree#search}.
   *
   * @param  word  <code>String</code> to search for
   *
   * @return  <code>boolean</code> - whether word was found
   */
  public boolean search(final String word)
  {
    if (this.ignoreCase) {
      return this.tree.search(word.toLowerCase());
    } else {
      return this.tree.search(word);
    }
  }


  /**
   * This will return an array of strings which partially match the supplied
   * word. This search is case sensitive. See {@link TernaryTree#partialSearch}.
   *
   * @param  word  <code>String</code> to search for
   *
   * @return  <code>String[]</code> - of matching words
   */
  public String[] partialSearch(final String word)
  {
    if (this.ignoreCase) {
      return this.tree.partialSearch(word.toLowerCase());
    } else {
      return this.tree.partialSearch(word);
    }
  }


  /**
   * This will return an array of strings which are near to the supplied word by
   * the supplied distance. This search is case sensitive. See {@link
   * TernaryTree#nearSearch}.
   *
   * @param  word  <code>String</code> to search for
   * @param  distance  <code>int</code> for valid match
   *
   * @return  <code>String[]</code> - of matching words
   */
  public String[] nearSearch(final String word, final int distance)
  {
    if (this.ignoreCase) {
      return this.tree.nearSearch(word.toLowerCase(), distance);
    } else {
      return this.tree.nearSearch(word, distance);
    }
  }


  /**
   * This will return an array of all the words in this <code>Dictionary</code>.
   *
   * @return  <code>String[]</code> - of words
   */
  public String[] getWords()
  {
    return this.words.toArray(new String[0]);
  }


  /**
   * This will return the number of words in this <code>Dictionary</code>.
   *
   * @return  <code>int</code> - number of words
   */
  public int getCount()
  {
    return this.words.size();
  }


  /**
   * This will cause data inserted into the <code>Dictionary</code> to be
   * inserted beginning from the median. The default behavior inserts data from
   * the beginning of any array or file which is inserted.
   */
  public void useMedian()
  {
    this.median = true;
  }


  /**
   * This will cause data inserted into the <code>Dictionary</code> to be
   * inserted in all lowercase. The default behavior inserts data when
   * capitalization preserved. This method must be called before any data is
   * inserted into the dictionary.
   */
  public void ignoreCase()
  {
    this.ignoreCase = true;
  }


  /**
   * This will print the contents of this <code>Dictionary</code> to the
   * supplied PrintWriter. The output will show One word per line.
   *
   * @param  out  <code>PrintWriter</code> to print to
   */
  public void print(final PrintWriter out)
  {
    final Iterator i = this.words.iterator();
    while (i.hasNext()) {
      out.println(i.next());
    }
  }


  /**
   * This will print the contents of this <code>Dictionary</code> to the
   * supplied PrintWriter. The output will attempt to show a tree structure.
   *
   * @param  out  <code>PrintWriter</code> to print to
   */
  public void printTree(final PrintWriter out)
  {
    this.tree.print(out);
  }


  /**
   * This provides command line access to a <code>Dictionary</code>.
   *
   * @param  args  <code>String[]</code>
   *
   * @throws  Exception  if an error occurs
   */
  public static void main(final String[] args)
    throws Exception
  {
    final Dictionary dict = new Dictionary();
    final List<File> files = new ArrayList<File>();
    try {
      if (args.length == 0) {
        throw new ArrayIndexOutOfBoundsException();
      }

      // sorting algorithms
      boolean bsort = false;
      boolean ssort = false;
      boolean isort = false;
      boolean qsort = false;

      // dictionary operations
      boolean count = false;
      boolean search = false;
      boolean partialSearch = false;
      boolean nearSearch = false;
      boolean print = false;
      boolean printTree = false;

      // operation parameters
      String word = null;
      int distance = 0;

      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-m")) {
          dict.useMedian();
        } else if (args[i].equals("-bsort")) {
          bsort = true;
        } else if (args[i].equals("-ssort")) {
          ssort = true;
        } else if (args[i].equals("-isort")) {
          isort = true;
        } else if (args[i].equals("-qsort")) {
          qsort = true;
        } else if (args[i].equals("-ci")) {
          dict.ignoreCase();
        } else if (args[i].equals("-c")) {
          count = true;
        } else if (args[i].equals("-s")) {
          search = true;
          word = args[++i];
        } else if (args[i].equals("-ps")) {
          partialSearch = true;
          word = args[++i];
        } else if (args[i].equals("-ns")) {
          nearSearch = true;
          word = args[++i];
          distance = Integer.parseInt(args[++i]);
        } else if (args[i].equals("-p")) {
          print = true;
        } else if (args[i].equals("-pt")) {
          printTree = true;
        } else if (args[i].equals("-h")) {
          throw new ArrayIndexOutOfBoundsException();
        } else {
          files.add(new File(args[i]));
        }
      }

      // insert data
      final Iterator<File> i = files.iterator();
      while (i.hasNext()) {
        dict.insert(i.next());
      }

      // do any sorting
      if (bsort) {
        dict.bubbleSort();
      }
      if (ssort) {
        dict.selectionSort();
      }
      if (isort) {
        dict.insertionSort();
      }
      if (qsort) {
        dict.quickSort();
      }

      // perform operation
      dict.build();
      if (count) {
        System.out.println(
          "This dictionary contains " + dict.getCount() + " words");
      } else if (search) {
        if (dict.search(word)) {
          System.out.println(word + " was found in this dictionary");
        } else {
          System.out.println(word + " was not found in this dictionary");
        }
      } else if (partialSearch) {
        final String[] matches = dict.partialSearch(word);
        System.out.println(
          "Found " + matches.length + " matches for " + word +
          " in this dictionary :");
        for (int j = 0; j < matches.length; j++) {
          System.out.println(matches[j]);
        }
      } else if (nearSearch) {
        final String[] matches = dict.nearSearch(word, distance);
        System.out.println(
          "Found " + matches.length + " matches for " + word +
          " in this dictionary at a distance of " + distance + " :");
        for (String s : matches) {
          System.out.println(s);
        }
      } else if (print) {
        dict.print(new PrintWriter(System.out, true));
      } else if (printTree) {
        dict.printTree(new PrintWriter(System.out, true));
      }

    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: java " + dict.getClass().getName() + " \\");
      System.out.println(
        "       <dictionary1> <dictionary2> ... " +
        "<options> <operation> \\");
      System.out.println("");
      System.out.println("where <options> includes:");
      System.out.println("       -m (Insert dictionary using it's median) \\");
      System.out.println("       -bsort (Bubble sort dictionary) \\");
      System.out.println("       -ssort (Selection sort dictionary) \\");
      System.out.println("       -isort (Insertion sort dictionary) \\");
      System.out.println("       -qsort (Quick sort dictionary) \\");
      System.out.println("       -ci (Make search case-insensitive) \\");
      System.out.println("");
      System.out.println("where <operation> includes:");
      System.out.println("       -c (Count words) \\");
      System.out.println("       -s <word> (Search for a word) \\");
      System.out.println("       -ps <word> (Partial search for a word) \\");
      System.out.println("           (where word like '.a.a.a') \\");
      System.out.println(
        "       -ns <word> <distance> " +
        "(Near search for a word) \\");
      System.out.println("       -p (Print the entire dictionary) \\");
      System.out.println(
        "       -pt (Print the entire dictionary " +
        "in tree form) \\");
      System.out.println("       -h (Print this message) \\");
      System.exit(1);
    }
  }
}
