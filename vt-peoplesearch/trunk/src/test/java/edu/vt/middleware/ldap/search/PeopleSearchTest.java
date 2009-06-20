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
package edu.vt.middleware.ldap.search;

import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import edu.vt.middleware.ldap.search.PeopleSearch.OutputFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link PeopleSearch}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PeopleSearchTest
{

  /** Entries for pool tests. */
  private static Map<String, LdapEntry[]> entries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of entries.
   */
  static {
    for (int i = 5; i <= 9; i++) {
      entries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** PeopleSearch to test. */
  private PeopleSearch search;


  /**
   * Default constructor.
   *
   * @throws  Exception  On test failure.
   */
  public PeopleSearchTest()
    throws Exception
  {
    final SearchInvoker searchInvoker = new SearchInvoker();
    searchInvoker.setProxySaslAuthorization(true);

    final Search s = new Search(1);
    s.setAdditive(true);
    s.getQueries().put(
      new Integer(1),
      "(|(givenName=@@@QUERY_1@@@)(sn=@@@QUERY_1@@@))");
    s.getQueries().put(
      new Integer(2),
      "(|(givenName=@@@QUERY_1@@@*)(sn=@@@QUERY_1@@@*))");
    s.getQueries().put(
      new Integer(3),
      "(|(givenName=*@@@QUERY_1@@@*)(sn=*@@@QUERY_1@@@*))");
    s.getQueries().put(
      new Integer(4),
      "(|(departmentNumber=@@@QUERY_1@@@)(mail=@@@QUERY_1@@@))");
    s.getQueries().put(
      new Integer(5),
      "(|(departmentNumber=@@@QUERY_1@@@*)(mail=@@@QUERY_1@@@*))");
    s.getQueries().put(
      new Integer(6),
      "(|(departmentNumber=*@@@QUERY_1@@@*)(mail=*@@@QUERY_1@@@*))");
    searchInvoker.getSearches().put(new Integer(1), s);

    final LdapPoolManager lpm = new LdapPoolManager();
    lpm.setLdapProperties("/ldap.properties");
    searchInvoker.setLdapPoolManager(lpm);

    this.search = new PeopleSearch(searchInvoker);
  }


  /**
   * @param  ldifFile5  to create.
   * @param  ldifFile6  to create.
   * @param  ldifFile7  to create.
   * @param  ldifFile8  to create.
   * @param  ldifFile9  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "createEntry5",
      "createEntry6",
      "createEntry7",
      "createEntry8",
      "createEntry9"
    }
  )
  @BeforeClass(groups = {"searchtest"})
  public void createEntry(
    final String ldifFile5,
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9)
    throws Exception
  {
    entries.get("5")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile5));
    entries.get("6")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile6));
    entries.get("7")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile7));
    entries.get("8")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile8));
    entries.get("9")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile9));

    Ldap ldap = TestUtil.createSetupLdap();
    for (Map.Entry<String, LdapEntry[]> e : entries.entrySet()) {
      ldap.create(
        e.getValue()[0].getDn(),
        e.getValue()[0].getLdapAttributes().toAttributes());
    }
    ldap.close();

    ldap = TestUtil.createLdap();
    for (Map.Entry<String, LdapEntry[]> e : entries.entrySet()) {
      while (
        !ldap.compare(
            e.getValue()[0].getDn(),
            e.getValue()[0].getDn().split(",")[0])) {
        Thread.sleep(100);
      }
    }
    ldap.close();
  }


  /**
   * @param  ldifFile5  to load.
   * @param  ldifFile6  to load.
   * @param  ldifFile7  to load.
   * @param  ldifFile8  to load.
   * @param  ldifFile9  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchResults5",
      "searchResults6",
      "searchResults7",
      "searchResults8",
      "searchResults9"
    }
  )
  @BeforeClass(groups = {"searchtest"})
  public void loadSearchResults(
    final String ldifFile5,
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9)
    throws Exception
  {
    entries.get("5")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile5));
    entries.get("6")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile6));
    entries.get("7")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile7));
    entries.get("8")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile8));
    entries.get("9")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile9));
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"searchtest"})
  public void deletePoolEntry()
    throws Exception
  {
    final Ldap ldap = TestUtil.createSetupLdap();
    ldap.delete(entries.get("5")[0].getDn());
    ldap.delete(entries.get("6")[0].getDn());
    ldap.delete(entries.get("7")[0].getDn());
    ldap.delete(entries.get("8")[0].getDn());
    ldap.delete(entries.get("9")[0].getDn());
    ldap.close();
  }


  /**
   * Sample user data.
   *
   * @return  user data
   */
  @DataProvider(name = "search-data")
  public Object[][] createTestData()
  {
    final LdapResult allResults = new LdapResult();
    allResults.addEntry(entries.get("5")[1]);
    allResults.addEntry(entries.get("6")[1]);
    allResults.addEntry(entries.get("7")[1]);
    allResults.addEntry(entries.get("8")[1]);
    allResults.addEntry(entries.get("9")[1]);

    final LdapResult allJames = new LdapResult();
    allJames.addEntry(entries.get("6")[1]);
    allJames.addEntry(entries.get("7")[1]);
    allJames.addEntry(entries.get("8")[1]);

    final LdapResult allRoosevelt = new LdapResult();
    allRoosevelt.addEntry(entries.get("5")[1]);
    allRoosevelt.addEntry(entries.get("9")[1]);

    return
      new Object[][] {
        {
          "1600",
          "departmentNumber|givenName|sn",
          allResults,
        },
        {
          "james",
          "departmentNumber|givenName|sn",
          allJames,
        },
        {
          "roosevelt",
          "departmentNumber|givenName|sn",
          allRoosevelt,
        },
        {
          "fdr",
          "departmentNumber|givenName|sn",
          new LdapResult(entries.get("5")[1]),
        },
        {
          "jm",
          "departmentNumber|givenName|sn",
          new LdapResult(entries.get("6")[1]),
        },
        {
          "jag",
          "departmentNumber|givenName|sn",
          new LdapResult(entries.get("7")[1]),
        },
        {
          "jec",
          "departmentNumber|givenName|sn",
          new LdapResult(entries.get("8")[1]),
        },
        {
          "tdr",
          "departmentNumber|givenName|sn",
          new LdapResult(entries.get("9")[1]),
        },
      };
  }


  /**
   * @param  query  to search with.
   * @param  returnAttrs  to search for.
   * @param  result  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"searchtest"},
    dataProvider = "search-data",
    threadPoolSize = 3,
    invocationCount = 50,
    timeOut = 60000
  )
  public void search(
    final String query,
    final String returnAttrs,
    final LdapResult result)
    throws Exception
  {
    final Query q = new Query();
    q.setLdapQuery(query);
    q.setQueryAttributes(returnAttrs.split("\\|"));

    final String searchResult = this.search.searchToString(
      q,
      OutputFormat.LDIF);
    AssertJUnit.assertEquals(
      result,
      TestUtil.convertLdifToResult(searchResult));
  }
}
