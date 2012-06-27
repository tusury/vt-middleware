/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.asn1;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ldaptive.LdapUtils;

/**
 * Describes paths to individual elements of an encoded DER object that may be
 * addressed during parsing to associate a parsed element with a handler to
 * process that element. Consider the following production rule for a complex
 * type that may be DER encoded:
 *
 * <pre>
 *
 *   BankAccountSet ::= SET OF {
 *     account BankAccount
 *   }
 *
 *   BankAccount ::= SEQUENCE OF {
 *     accountNumber OCTET STRING,
 *     accountName OCTET STRING,
 *     accountType AccountType,
 *     balance REAL
 *   }
 *
 *   AccountType ::= ENUM {
 *     checking (0),
 *     savings (1)
 *   }
 *
 * </pre>
 *
 * <p>Given a BankAccountSet type with two elements, the path to the balance of
 * the second account is given by the following canonical path (using tag names
 * from {@link UniversalDERTag}):</p>
 *
 * <pre>/SET[1]/SEQ/REAL</pre>
 *
 * <p>The index of the first element in a collection type
 * (e.g. <code>SET</code>) is optional.  Moreover the canonical path given by
 * {@link #toString()} does not print the index to the first item in a
 * collection.</p>
 *
 * <p>Context-specific and application-specific tags are handled in a general
 * manner via the use of tags of the form <code>CTX(n)</code> and
 * <code>APP(n)</code> where <code>n</code> is the tag
 * number.</p>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 * @see DERParser
 */
public class DERPath
{
  /** Separates nodes in a path specification. */
  public static final String PATH_SEPARATOR = "/";

  /** Pattern for matching nodes. */
  public static final Pattern NODE_PATTERN = Pattern.compile(
      String.format(
          "(([A-Za-z]+)|(((%s)|(%s))\\(\\d+\\)))(\\[(\\d+)\\])?",
          ApplicationDERTag.TAG_NAME,
          ContextDERTag.TAG_NAME));

  /** Pattern group index for matching the child index. */
  private static final int CHILD_INDEX_PATTERN_GROUP = 8;

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 601;

  /** Path nodes in this DER path. */
  private final List<Node> nodeList = new ArrayList<Node>();


  /** Creates an empty path specification. */
  public DERPath()
  {
    this(PATH_SEPARATOR);
  }


  /**
   * Copy constructor.
   *
   * @param  path  to read nodes from
   */
  public DERPath(final DERPath path)
  {
    nodeList.addAll(path.nodeList);
  }


  /**
   * Creates a path specification from its string representation.
   *
   * @param  pathSpec  string representation of a path, e.g. /SEQ[1]/CHOICE.
   */
  public DERPath(final String pathSpec)
  {
    final String[] nodes = pathSpec.split(PATH_SEPARATOR);
    for (String node : nodes) {
      if ("".equals(node)) {
        continue;
      }

      final Matcher matcher = NODE_PATTERN.matcher(node);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid node name " + node);
      }

      final String tagName = matcher.group(1);
      int childIndex = 0;
      if (matcher.group(CHILD_INDEX_PATTERN_GROUP) != null) {
        childIndex = Integer.parseInt(matcher.group(CHILD_INDEX_PATTERN_GROUP));
      }
      pushChild(tagName, childIndex);
    }
  }


  /**
   * Adds a node with the supplied name at beginning of the path.
   *
   * @param  name  of the path to add
   */
  public void pushChild(final String name)
  {
    pushChild(name, 0);
  }


  /**
   * Adds a node with the supplied name at the supplied path index.
   *
   * @param  name  of the path to add
   * @param  index  location in the path
   */
  public void pushChild(final String name, final int index)
  {
    if (index < 0) {
      throw new IllegalArgumentException("Child index must be non-negative.");
    }
    nodeList.add(new Node(name, index));
  }


  /**
   * Removes the last node in the path.
   *
   * @return  last node in the path
   */
  public Node popChild()
  {
    return nodeList.remove(nodeList.size() - 1);
  }


  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o)
  {
    return LdapUtils.areEqual(this, o);
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtils.computeHashCode(HASH_CODE_SEED, nodeList);
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder(nodeList.size() * 10);
    for (Node n : nodeList) {
      sb.append(PATH_SEPARATOR).append(n.getName());
      if (n.childIndex > 0) {
        sb.append('[').append(n.getChildIndex()).append(']');
      }
    }
    return sb.toString();
  }


  /**
   * Node which encapsulates the path name and it's location in the path.
   *
   * @author  Middleware Services
   * @version  $Revision$ $Date$
   */
  private class Node
  {

    /** hash code seed. */
    private static final int HASH_CODE_SEED = 607;

    /** Name of this node. */
    private final String name;

    /** Index of this node. */
    private final int childIndex;


    /**
     * Creates a new node.
     *
     * @param  n  name of this node
     * @param  i  child index location of this node in the path
     */
    public Node(final String n, final int i)
    {
      name = n;
      childIndex = i;
    }


    /**
     * Returns the name.
     *
     * @return  name
     */
    public String getName()
    {
      return name;
    }


    /**
     * Returns the child index.
     *
     * @return  child index
     */
    public int getChildIndex()
    {
      return childIndex;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o)
    {
      return LdapUtils.areEqual(this, o);
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
      return LdapUtils.computeHashCode(HASH_CODE_SEED, name, childIndex);
    }


    /** {@inheritDoc} */
    @Override
    public String toString()
    {
      return String.format("%s[%s]", name, childIndex);
    }
  }
}
