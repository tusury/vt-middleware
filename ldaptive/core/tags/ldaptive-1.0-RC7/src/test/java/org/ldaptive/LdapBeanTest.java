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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SearchResult}, {@link LdapEntry}, and
 * {@link LdapAttribute}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapBeanTest
{


  /**
   * Tests aspects of ldap result.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"bean"})
  public void ldapResult()
    throws Exception
  {
    final LdapEntry entry1 = new LdapEntry("uid=1");
    final LdapEntry entry2 = new LdapEntry("uid=2");

    // test default sort behavior
    SearchResult sr = new SearchResult();
    AssertJUnit.assertEquals(
      SortBehavior.getDefaultSortBehavior(), sr.getSortBehavior());
    AssertJUnit.assertEquals(0, sr.size());
    AssertJUnit.assertNull(sr.getEntry());
    sr.clear();
    AssertJUnit.assertEquals(0, sr.size());

    // test ordered
    sr = new SearchResult(SortBehavior.ORDERED);
    AssertJUnit.assertEquals(SortBehavior.ORDERED, sr.getSortBehavior());
    sr.addEntry(entry2, entry1);
    LdapEntry[] entries = sr.getEntries().toArray(new LdapEntry[2]);
    AssertJUnit.assertEquals(entry2, entries[0]);
    AssertJUnit.assertEquals(entry1, entries[1]);
    sr.clear();
    AssertJUnit.assertEquals(0, sr.size());

    // test sorted
    sr = new SearchResult(SortBehavior.SORTED);
    AssertJUnit.assertEquals(SortBehavior.SORTED, sr.getSortBehavior());
    sr.addEntry(entry2, entry1);
    entries = sr.getEntries().toArray(new LdapEntry[2]);
    AssertJUnit.assertEquals(entry1, entries[0]);
    AssertJUnit.assertEquals(entry2, entries[1]);
    sr.clear();
    AssertJUnit.assertEquals(0, sr.size());

    // test create with one entry
    sr = new SearchResult(entry1);
    AssertJUnit.assertEquals(entry1, sr.getEntry());
    AssertJUnit.assertEquals(entry1, sr.getEntry("uid=1"));
    AssertJUnit.assertEquals(entry1, sr.getEntry("UID=1"));
    AssertJUnit.assertEquals("uid=1", sr.getEntryDns()[0]);
    AssertJUnit.assertEquals(1, sr.size());
    AssertJUnit.assertEquals(sr, new SearchResult(entry1));
    sr.clear();
    AssertJUnit.assertEquals(0, sr.size());

    // test create with two entries
    sr = new SearchResult(entry2, entry1);
    AssertJUnit.assertEquals(entry1, sr.getEntry("uid=1"));
    AssertJUnit.assertEquals(entry2, sr.getEntry("UID=2"));
    AssertJUnit.assertEquals(2, sr.getEntryDns().length);
    AssertJUnit.assertEquals(2, sr.size());
    AssertJUnit.assertEquals(sr, new SearchResult(entry1, entry2));
    sr.removeEntry(entry2);
    AssertJUnit.assertEquals(1, sr.size());
    sr.clear();
    AssertJUnit.assertEquals(0, sr.size());

    // test create with collection
    final Set<LdapEntry> s = new HashSet<LdapEntry>();
    s.add(entry1);
    sr = new SearchResult(s);
    sr.addEntry(entry2);
    AssertJUnit.assertEquals(entry1, sr.getEntry("UID=1"));
    AssertJUnit.assertEquals(entry2, sr.getEntry("uid=2"));
    AssertJUnit.assertEquals(2, sr.getEntryDns().length);
    AssertJUnit.assertEquals(2, sr.size());
    AssertJUnit.assertEquals(sr, new SearchResult(entry1, entry2));
    sr.removeEntry("UID=1");
    AssertJUnit.assertEquals(1, sr.size());
    sr.clear();
    AssertJUnit.assertEquals(0, sr.size());

    // test sub results
    sr = new SearchResult(SortBehavior.SORTED);
    sr.addEntry(entry2, entry1);
    AssertJUnit.assertEquals(0, sr.subResult(2, 2).size());
    AssertJUnit.assertEquals(1, sr.subResult(1, 2).size());
    AssertJUnit.assertEquals(2, sr.subResult(0, 2).size());
    try {
      sr.subResult(-1, 1);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IndexOutOfBoundsException.class, e.getClass());
    }
    try {
      sr.subResult(0, 3);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IndexOutOfBoundsException.class, e.getClass());
    }
    try {
      sr.subResult(1, 0);
      AssertJUnit.fail("Should have thrown IndexOutOfBoundsException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IndexOutOfBoundsException.class, e.getClass());
    }
  }


  /**
   * Tests aspects of ldap entry.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"bean"})
  public void ldapEntry()
    throws Exception
  {
    final LdapAttribute attr1 = new LdapAttribute("givenName", "John");
    final LdapAttribute attr2 = new LdapAttribute("sn", "Doe");

    // test default sort behavior
    LdapEntry le = new LdapEntry("uid=1");
    AssertJUnit.assertEquals(
      SortBehavior.getDefaultSortBehavior(), le.getSortBehavior());
    AssertJUnit.assertEquals(0, le.size());
    AssertJUnit.assertNull(le.getAttribute());
    AssertJUnit.assertEquals("uid=1", le.getDn());
    le.setDn("uid=2");
    AssertJUnit.assertEquals("uid=2", le.getDn());
    le.clear();
    AssertJUnit.assertEquals(0, le.size());

    // test ordered
    le = new LdapEntry(SortBehavior.ORDERED);
    AssertJUnit.assertEquals(SortBehavior.ORDERED, le.getSortBehavior());
    le.addAttribute(attr2, attr1);
    LdapAttribute[] attrs = le.getAttributes().toArray(new LdapAttribute[2]);
    AssertJUnit.assertEquals(attr2, attrs[0]);
    AssertJUnit.assertEquals(attr1, attrs[1]);
    le.clear();
    AssertJUnit.assertEquals(0, le.size());

    // test sorted
    le = new LdapEntry(SortBehavior.SORTED);
    AssertJUnit.assertEquals(SortBehavior.SORTED, le.getSortBehavior());
    le.addAttribute(attr2, attr1);
    attrs = le.getAttributes().toArray(new LdapAttribute[2]);
    AssertJUnit.assertEquals(attr1, attrs[0]);
    AssertJUnit.assertEquals(attr2, attrs[1]);
    le.clear();
    AssertJUnit.assertEquals(0, le.size());

    // test create with one entry
    le = new LdapEntry("uid=1", attr1);
    AssertJUnit.assertEquals(attr1, le.getAttribute());
    AssertJUnit.assertEquals(attr1, le.getAttribute("givenName"));
    AssertJUnit.assertEquals(attr1, le.getAttribute("givenname"));
    AssertJUnit.assertEquals("givenName", le.getAttributeNames()[0]);
    AssertJUnit.assertEquals(1, le.size());
    AssertJUnit.assertEquals(le, new LdapEntry("uid=1", attr1));
    le.clear();
    AssertJUnit.assertEquals(0, le.size());

    // test create with two entries
    le = new LdapEntry("uid=1", attr2, attr1);
    AssertJUnit.assertEquals(attr1, le.getAttribute("givenName"));
    AssertJUnit.assertEquals(attr2, le.getAttribute("SN"));
    AssertJUnit.assertEquals(2, le.getAttributeNames().length);
    AssertJUnit.assertEquals(2, le.size());
    AssertJUnit.assertEquals(le, new LdapEntry("uid=1", attr1, attr2));
    le.removeAttribute(attr2);
    AssertJUnit.assertEquals(1, le.size());
    le.clear();
    AssertJUnit.assertEquals(0, le.size());

    // test create with collection
    final Set<LdapAttribute> s = new HashSet<LdapAttribute>();
    s.add(attr1);
    le = new LdapEntry("uid=1", s);
    le.addAttribute(attr2);
    AssertJUnit.assertEquals(attr1, le.getAttribute("GIVENNAME"));
    AssertJUnit.assertEquals(attr2, le.getAttribute("sn"));
    AssertJUnit.assertEquals(2, le.getAttributeNames().length);
    AssertJUnit.assertEquals(2, le.size());
    AssertJUnit.assertEquals(le, new LdapEntry("uid=1", attr1, attr2));
    le.removeAttribute("GIVENNAME");
    AssertJUnit.assertEquals(1, le.size());
    le.clear();
    AssertJUnit.assertEquals(0, le.size());
  }


  /**
   * Tests aspects of ldap attribute.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"bean"})
  public void ldapAttribute()
    throws Exception
  {
    // test default sort behavior
    LdapAttribute la = new LdapAttribute("givenName");
    AssertJUnit.assertEquals(
      SortBehavior.getDefaultSortBehavior(), la.getSortBehavior());
    AssertJUnit.assertEquals(0, la.size());
    AssertJUnit.assertNull(la.getStringValue());
    AssertJUnit.assertNull(la.getBinaryValue());
    AssertJUnit.assertEquals("givenName", la.getName());
    la.setName("sn");
    AssertJUnit.assertEquals("sn", la.getName());
    la.clear();
    AssertJUnit.assertEquals(0, la.size());

    // test ordered
    la = new LdapAttribute(SortBehavior.ORDERED);
    AssertJUnit.assertEquals(SortBehavior.ORDERED, la.getSortBehavior());
    la.addStringValue("William", "Bill");
    String[] values = la.getStringValues().toArray(new String[2]);
    AssertJUnit.assertEquals("William", values[0]);
    AssertJUnit.assertEquals("Bill", values[1]);
    la.clear();
    AssertJUnit.assertEquals(0, la.size());

    // test sorted
    la = new LdapAttribute(SortBehavior.SORTED);
    AssertJUnit.assertEquals(SortBehavior.SORTED, la.getSortBehavior());
    la.addStringValue("William", "Bill");
    values = la.getStringValues().toArray(new String[2]);
    AssertJUnit.assertEquals("Bill", values[0]);
    AssertJUnit.assertEquals("William", values[1]);
    la.clear();
    AssertJUnit.assertEquals(0, la.size());

    // test create with one entry
    la = new LdapAttribute("givenName", "William");
    AssertJUnit.assertEquals("William", la.getStringValue());
    AssertJUnit.assertEquals(1, la.getStringValues().size());
    AssertJUnit.assertEquals("William", la.getStringValues().iterator().next());
    AssertJUnit.assertTrue(
      Arrays.equals("William".getBytes(), la.getBinaryValue()));
    AssertJUnit.assertEquals(1, la.size());
    AssertJUnit.assertEquals(la, new LdapAttribute("givenName", "William"));
    try {
      la.addStringValue((String) null);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }
    try {
      la.addBinaryValue("Bill".getBytes());
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }
    la.clear();
    AssertJUnit.assertEquals(0, la.size());

    // test create with two entries
    la = new LdapAttribute("givenName", "Bill", "William");
    AssertJUnit.assertEquals(2, la.getStringValues().size());
    AssertJUnit.assertEquals(2, la.size());
    AssertJUnit.assertEquals(
      la, new LdapAttribute("givenName", "William", "Bill"));
    la.removeStringValue("William");
    AssertJUnit.assertEquals(1, la.size());
    la.clear();
    AssertJUnit.assertEquals(0, la.size());

    // test binary values
    la = new LdapAttribute("jpegPhoto", "image".getBytes());
    AssertJUnit.assertTrue(
      Arrays.equals("image".getBytes(), la.getBinaryValue()));
    AssertJUnit.assertEquals(1, la.getBinaryValues().size());
    AssertJUnit.assertEquals("aW1hZ2U=", la.getStringValue());
    AssertJUnit.assertEquals(1, la.getStringValues().size());
    AssertJUnit.assertEquals(
      la, new LdapAttribute("jpegPhoto", "image".getBytes()));
    try {
      la.addBinaryValue((byte[]) null);
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }
    try {
      la.addStringValue("Bill");
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }
    la.clear();
    AssertJUnit.assertEquals(0, la.size());

    // test attribute options
    la = new LdapAttribute("cn", "William Wallace");
    AssertJUnit.assertEquals("cn", la.getName());
    AssertJUnit.assertEquals("cn", la.getName(true));
    AssertJUnit.assertEquals("cn", la.getName(false));
    AssertJUnit.assertNotNull(la.getOptions());
    AssertJUnit.assertEquals(0, la.getOptions().length);

    la = new LdapAttribute("cn;lang-ru", "Уильям Уоллес");
    AssertJUnit.assertEquals("cn;lang-ru", la.getName());
    AssertJUnit.assertEquals("cn;lang-ru", la.getName(true));
    AssertJUnit.assertEquals("cn", la.getName(false));
    AssertJUnit.assertNotNull(la.getOptions());
    AssertJUnit.assertEquals(1, la.getOptions().length);
    AssertJUnit.assertEquals("lang-ru", la.getOptions()[0]);

    la = new LdapAttribute("cn;lang-lv;dynamic", "Viljams Voless");
    AssertJUnit.assertEquals("cn;lang-lv;dynamic", la.getName());
    AssertJUnit.assertEquals("cn;lang-lv;dynamic", la.getName(true));
    AssertJUnit.assertEquals("cn", la.getName(false));
    AssertJUnit.assertNotNull(la.getOptions());
    AssertJUnit.assertEquals(2, la.getOptions().length);
    AssertJUnit.assertEquals("lang-lv", la.getOptions()[0]);
    AssertJUnit.assertEquals("dynamic", la.getOptions()[1]);
  }
}
