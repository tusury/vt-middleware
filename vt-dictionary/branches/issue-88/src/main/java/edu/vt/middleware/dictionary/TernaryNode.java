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

/**
 * <code>TernaryNode</code> is an implementation of a node contained in a
 * ternary tree.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class TernaryNode
{

  /** character of this node. */
  private char splitchar;

  /** word contained in node. */
  private String word;

  /** low child of this node. */
  private TernaryNode lokid;

  /** equal child of this node. */
  private TernaryNode eqkid;

  /** high child of this node. */
  private TernaryNode hikid;


  /**
   * This will create a new <code>TernaryNode</code> with the supplied
   * character.
   *
   * @param  c  <code>char</code>
   */
  public TernaryNode(final char c)
  {
    this.splitchar = c;
  }


  /**
   * This returns the splitchar of this <code>TernaryNode</code>.
   *
   * @return  <code>char</code>
   */
  public char getSplitChar()
  {
    return this.splitchar;
  }


  /**
   * This sets the splitchar for this <code>TernaryNode</code>.
   *
   * @param  c  <code>char</code>
   */
  public void setSplitChar(final char c)
  {
    this.splitchar = c;
  }


  /**
   * @return the word
   */
  public String getWord()
  {
    return word;
  }


  /**
   * @param w the word to set
   */
  public void setWord(final String w)
  {
    this.word = w;
  }


  /**
   * This returns the lokid of this <code>TernaryNode</code>.
   *
   * @return  <code>TernaryNode</code>
   */
  public TernaryNode getLokid()
  {
    return this.lokid;
  }


  /**
   * This sets the lokid of this <code>TernaryNode</code>.
   *
   * @param  node  <code>TernaryNode</code>
   */
  public void setLokid(final TernaryNode node)
  {
    this.lokid = node;
  }


  /**
   * This returns the eqkid of this <code>TernaryNode</code>.
   *
   * @return  <code>TernaryNode</code>
   */
  public TernaryNode getEqkid()
  {
    return this.eqkid;
  }


  /**
   * This sets the eqkid of this <code>TernaryNode</code>.
   *
   * @param  node  <code>TernaryNode</code>
   */
  public void setEqkid(final TernaryNode node)
  {
    this.eqkid = node;
  }


  /**
   * This returns the hikid of this <code>TernaryNode</code>.
   *
   * @return  <code>TernaryNode</code>
   */
  public TernaryNode getHikid()
  {
    return this.hikid;
  }


  /**
   * This sets the hikid of this <code>TernaryNode</code>.
   *
   * @param  node  <code>TernaryNode</code>
   */
  public void setHikid(final TernaryNode node)
  {
    this.hikid = node;
  }
}
