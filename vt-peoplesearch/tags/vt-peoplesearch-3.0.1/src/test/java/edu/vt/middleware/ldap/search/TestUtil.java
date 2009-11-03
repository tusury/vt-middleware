/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.bean.LdapAttribute;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import org.testng.annotations.DataProvider;

/**
 * Common methods for people search tests.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public final class TestUtil
{


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "setup-ldap")
  public static Ldap createSetupLdap()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.setup.properties"));
    return l;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "ldap")
  public static Ldap createLdap()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.properties"));
    return l;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "digest-md5-ldap")
  public static Ldap createDigestMD5Ldap()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.digest-md5.properties"));
    return l;
  }


  /**
   * Reads a file on the classpath into a reader.
   *
   * @param  filename  to open.
   *
   * @return  reader.
   *
   * @throws  Exception  If file cannot be read.
   */
  public static BufferedReader readFile(final String filename)
    throws Exception
  {
    return
      new BufferedReader(
        new InputStreamReader(TestUtil.class.getResourceAsStream(filename)));
  }


  /**
   * Reads a file on the classpath into a string.
   *
   * @param  filename  to open.
   *
   * @return  string.
   *
   * @throws  Exception  If file cannot be read.
   */
  public static String readFileIntoString(final String filename)
    throws Exception
  {
    final StringBuffer result = new StringBuffer();
    final BufferedReader br = readFile(filename);
    try {
      String line;
      while ((line = br.readLine()) != null) {
        result.append(line).append(System.getProperty("line.separator"));
      }
    } finally {
      br.close();
    }
    return result.toString();
  }


  /**
   * Converts a ldif to a <code>LdapResult</code>.
   *
   * @param  ldif  to convert.
   *
   * @return  LdapResult.
   */
  public static LdapResult convertLdifToResult(final String ldif)
  {
    final LdapResult result = new LdapResult();
    final String[] entries = ldif.split(
      System.getProperty("line.separator") +
      System.getProperty("line.separator"));
    for (int i = 0; i < entries.length; i++) {
      result.addEntry(convertLdifToEntry(entries[i]));
    }
    return result;
  }


  /**
   * Converts a ldif to a <code>LdapEntry</code>.
   *
   * @param  ldif  to convert.
   *
   * @return  LdapEntry.
   */
  public static LdapEntry convertLdifToEntry(final String ldif)
  {
    final LdapEntry entry = new LdapEntry();
    final String[] lines = ldif.split(System.getProperty("line.separator"));
    for (int i = 0; i < lines.length; i++) {
      boolean isBinary = false;
      if (lines[i].indexOf("::") != -1) {
        isBinary = true;
      }

      final String[] parts = lines[i].trim().split(":* ", 2);
      if (parts[0] != null && !parts[0].equals("")) {
        if (parts[0].equalsIgnoreCase("dn")) {
          entry.setDn(parts[1]);
        } else {
          LdapAttribute la = entry.getLdapAttributes().getAttribute(parts[0]);
          if (la == null) {
            la = new LdapAttribute();
            la.setName(parts[0]);
            entry.getLdapAttributes().addAttribute(la);
          }
          if (isBinary) {
            la.getValues().add(LdapUtil.base64Decode(parts[1]));
          } else {
            la.getValues().add(parts[1]);
          }
        }
      }
    }
    return entry;
  }
}
