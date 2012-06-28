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
import java.util.Collections;
import java.util.List;

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
 * <p>Given an instance of BankAccountSet with two elements, the path to the
 * balance of each bank account in the set is given by the following
 * expression:</p>
 *
 * <pre>/SET/SEQ/REAL</pre>
 *
 * <p>Node names in DER paths are constrained to the following:</p>
 *
 * <ul>
 *   <li>{@link UniversalDERTag} tag names</li>
 *   <li>{@link ApplicationDERTag#TAG_NAME}</li>
 *   <li>{@link ContextDERTag#TAG_NAME}</li>
 * </ul>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 * @see DERParser
 */
public class DERPath
{
  /** Separates nodes in a path specification. */
  public static final String PATH_SEPARATOR = "/";

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 601;

  /** Describes the path as a FIFO set of nodes. */
  private final List<String> nodeList = new ArrayList<String>();


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
      validateNode(node.toUpperCase());
      pushNode(node);
    }
  }


  /**
   * Adds a node with the supplied name at beginning of the path.
   *
   * @param  name  of the path to add
   */
  public void pushNode(final String name)
  {
    nodeList.add(name);
  }


  /**
   * Examines the last node in the path without removing it.
   *
   * @return  last node in the path
   */
  public String peekNode()
  {
    return nodeList.get(nodeList.size() - 1);
  }


  /**
   * Removes the last node in the path.
   *
   * @return  last node in the path
   */
  public String popNode()
  {
    return nodeList.remove(nodeList.size() - 1);
  }


  /**
   * Gets an immutable list of nodes in this path where the left-most node is
   * the first element and the right-most node is last.
   *
   * @return  Immutable list of path nodes.
   */
  public List<String> getNodes() {
    return Collections.unmodifiableList(nodeList);
  }


  /**
   * Gets the number of nodes in the path.
   *
   * @return  node count.
   */
  public int getSize() {
    return nodeList.size();
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
    for (String n : nodeList) {
      sb.append(PATH_SEPARATOR).append(n);
    }
    return sb.toString();
  }


  /**
   * Determines whether a given canonical (uppercase) node name is valid.
   *
   * @param  canonicalNodeName  Canonical node name.
   *
   * @throws  IllegalArgumentException  for an invalid node name.
   */
  private void validateNode(final String canonicalNodeName) {
    final boolean isValid =
        UniversalDERTag.fromTagName(canonicalNodeName) != null ||
        canonicalNodeName.startsWith(ApplicationDERTag.TAG_NAME) ||
        canonicalNodeName.startsWith(ContextDERTag.TAG_NAME);
    if (!isValid) {
      throw new IllegalArgumentException(
          "Invalid node name: " + canonicalNodeName);
    }
  }
}
