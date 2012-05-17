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
package org.ldaptive;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Simple bean representing an ldap result. Contains a map of entry DN to ldap
 * entry.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapResult extends AbstractLdapBean
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 337;

  /** serial version uid. */
  private static final long serialVersionUID = 8716095444363430453L;

  /** Entries contained in this result. */
  private final Map<String, LdapEntry> resultEntries;


  /** Default constructor. */
  public LdapResult()
  {
    this(SortBehavior.getDefaultSortBehavior());
  }


  /**
   * Creates a new ldap result.
   *
   * @param  sb  sort behavior of the results
   */
  public LdapResult(final SortBehavior sb)
  {
    super(sb);
    if (SortBehavior.UNORDERED == sb) {
      resultEntries = new HashMap<String, LdapEntry>();
    } else if (SortBehavior.ORDERED == sb) {
      resultEntries = new LinkedHashMap<String, LdapEntry>();
    } else if (SortBehavior.SORTED == sb) {
      resultEntries = new TreeMap<String, LdapEntry>(
        String.CASE_INSENSITIVE_ORDER);
    } else {
      throw new IllegalArgumentException("Unknown sort behavior: " + sb);
    }
  }


  /**
   * Creates a new ldap result.
   *
   * @param  entry  ldap entry
   */
  public LdapResult(final LdapEntry... entry)
  {
    this();
    for (LdapEntry e : entry) {
      addEntry(e);
    }
  }


  /**
   * Creates a new ldap result.
   *
   * @param  entries  collection of ldap entries
   */
  public LdapResult(final Collection<LdapEntry> entries)
  {
    this();
    addEntries(entries);
  }


  /**
   * Returns a collection of ldap entry.
   *
   * @return  collection of ldap entry
   */
  public Collection<LdapEntry> getEntries()
  {
    return resultEntries.values();
  }


  /**
   * Returns a single entry of this result. If multiple entries exist the first
   * entry returned by the underlying iterator is used. If no entries exist null
   * is returned.
   *
   * @return  single entry
   */
  public LdapEntry getEntry()
  {
    if (resultEntries.isEmpty()) {
      return null;
    }
    return resultEntries.values().iterator().next();
  }


  /**
   * Returns the ldap in this result with the supplied DN.
   *
   * @param  dn  of the entry to return
   *
   * @return  ldap entry
   */
  public LdapEntry getEntry(final String dn)
  {
    return resultEntries.get(dn.toLowerCase());
  }


  /**
   * Returns the entry DNs in this result.
   *
   * @return  string array of entry DNs
   */
  public String[] getEntryDns()
  {
    return
      resultEntries.keySet().toArray(new String[resultEntries.keySet().size()]);
  }


  /**
   * Adds an entry to this ldap result.
   *
   * @param  entry  entry to add
   */
  public void addEntry(final LdapEntry... entry)
  {
    for (LdapEntry e : entry) {
      resultEntries.put(e.getDn().toLowerCase(), e);
    }
  }


  /**
   * Adds entry(s) to this ldap result.
   *
   * @param  entries  collection of entries to add
   */
  public void addEntries(final Collection<LdapEntry> entries)
  {
    for (LdapEntry e : entries) {
      addEntry(e);
    }
  }


  /**
   * Removes an entry from this ldap result.
   *
   * @param  entry  entry to remove
   */
  public void removeEntry(final LdapEntry... entry)
  {
    for (LdapEntry e : entry) {
      resultEntries.remove(e.getDn().toLowerCase());
    }
  }


  /**
   * Removes the entry with the supplied dn from this ldap result.
   *
   * @param  dn  of entry to remove
   */
  public void removeEntry(final String dn)
  {
    resultEntries.remove(dn.toLowerCase());
  }


  /**
   * Removes the entry(s) from this ldap result.
   *
   * @param  entries  collection of ldap entries to remove
   */
  public void removeEntries(final Collection<LdapEntry> entries)
  {
    for (LdapEntry le : entries) {
      removeEntry(le);
    }
  }


  /**
   * Returns a portion of this result between the specified fromIndex,
   * inclusive, and toIndex, exclusive. If fromIndex and toIndex are equal, the
   * return result is empty. The result of this method is undefined for
   * unordered results.
   *
   * @param  fromIndex  low endpoint of the ldap result (inclusive)
   * @param  toIndex  high endpoint of the ldap result (exclusive)
   *
   * @return  portion of this ldap result
   *
   * @throws  IndexOutOfBoundsException  for illegal index values
   */
  public LdapResult subResult(final int fromIndex, final int toIndex)
  {
    if (fromIndex < 0 ||
        toIndex > resultEntries.size() ||
        fromIndex > toIndex) {
      throw new IndexOutOfBoundsException("Illegal index value");
    }
    final LdapResult lr = new LdapResult(getSortBehavior());
    if (resultEntries.isEmpty() || fromIndex == toIndex) {
      return lr;
    }
    int i = 0;
    for (Map.Entry<String, LdapEntry> e : resultEntries.entrySet()) {
      if (i >= fromIndex && i < toIndex) {
        lr.addEntry(e.getValue());
      }
      i++;
    }
    return lr;
  }


  /**
   * Returns the number of entries in this ldap result.
   *
   * @return  number of entries in this ldap result
   */
  public int size()
  {
    return resultEntries.size();
  }


  /** Removes all the entries in this ldap result. */
  public void clear()
  {
    resultEntries.clear();
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtils.computeHashCode(HASH_CODE_SEED, resultEntries.values());
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return String.format("[%s]", resultEntries.values());
  }


  /**
   * Merges the entries in the supplied result into a single entry. This method
   * always returns a ldap result of size zero or one.
   *
   * @param  result  ldap result containing entries to merge
   *
   * @return  ldap result containing a single merged entry
   */
  public static LdapResult mergeEntries(final LdapResult result)
  {
    LdapEntry mergedEntry = null;
    if (result != null) {
      for (LdapEntry le : result.getEntries()) {
        if (mergedEntry == null) {
          mergedEntry = le;
        } else {
          for (LdapAttribute la : le.getAttributes()) {
            final LdapAttribute oldAttr = mergedEntry.getAttribute(
              la.getName());
            if (oldAttr == null) {
              mergedEntry.addAttribute(la);
            } else {
              if (oldAttr.isBinary()) {
                oldAttr.addBinaryValues(la.getBinaryValues());
              } else {
                oldAttr.addStringValues(la.getStringValues());
              }
            }
          }
        }
      }
    }
    return mergedEntry != null ? new LdapResult(mergedEntry) : new LdapResult();
  }
}
