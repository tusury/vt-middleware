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
import java.util.Iterator;
import java.util.Map;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.ldif.Ldif;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SearchInvoker}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SearchInvokerTest
{

  /** Entries for pool tests. */
  private static Map<String, LdapEntry[]> entries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of entries.
   */
  static {
    for (int i = 2; i <= 4; i++) {
      entries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** searchinvoker to test. */
  private SearchInvoker searchInvoker;


  /**
   * Default constructor.
   *
   * @throws  Exception  On test failure.
   */
  public SearchInvokerTest()
    throws Exception
  {
    this.searchInvoker = new SearchInvoker();
    this.searchInvoker.setProxySaslAuthorization(true);

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
    this.searchInvoker.getSearches().put(new Integer(1), s);

    final LdapPoolManager lpm = new LdapPoolManager();
    lpm.setLdapProperties("/ldap.digest-md5.properties");
    lpm.setLdapPoolProperties("/ldap.pool.properties");
    this.searchInvoker.setLdapPoolManager(lpm);
  }


  /**
   * @param  ldifFile2  to create.
   * @param  ldifFile3  to create.
   * @param  ldifFile4  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "createEntry2",
      "createEntry3",
      "createEntry4"
    })
  @BeforeClass(groups = {"invokertest"})
  public void createEntry(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4)
    throws Exception
  {
    entries.get("2")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile2));
    entries.get("3")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile3));
    entries.get("4")[0] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile4));

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
   * @param  ldifFile2  to load.
   * @param  ldifFile3  to load.
   * @param  ldifFile4  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "searchResults2",
      "searchResults3",
      "searchResults4"
    })
  @BeforeClass(groups = {"invokertest"})
  public void loadSearchResults(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4)
    throws Exception
  {
    entries.get("2")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile2));
    entries.get("3")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile3));
    entries.get("4")[1] = TestUtil.convertLdifToEntry(
      TestUtil.readFileIntoString(ldifFile4));
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"invokertest"})
  public void deletePoolEntry()
    throws Exception
  {
    final Ldap ldap = TestUtil.createSetupLdap();
    ldap.delete(entries.get("2")[0].getDn());
    ldap.delete(entries.get("3")[0].getDn());
    ldap.delete(entries.get("4")[0].getDn());
    ldap.close();
  }


  /**
   * Sample user data.
   *
   * @return  user data
   */
  @DataProvider(name = "invoker-data")
  public Object[][] createTestData()
  {
    return
      new Object[][] {
        {
          "Harry",
          "departmentNumber|givenName|sn|mail",
          "dn:uid=101,ou=test,dc=vt,dc=edu",
          entries.get("2")[1],
        },
        {
          "dwight",
          "departmentNumber|givenName|sn|mail",
          "dn:uid=102,ou=test,dc=vt,dc=edu",
          entries.get("3")[1],
        },
        {
          "john",
          "departmentNumber|givenName|sn|mail",
          "dn:uid=103,ou=test,dc=vt,dc=edu",
          entries.get("4")[1],
        },
      };
  }


  /**
   * @param  query  to search with.
   * @param  returnAttrs  to search for.
   * @param  saslAuthz  ID to authorize as.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"invokertest"},
    dataProvider = "invoker-data",
    threadPoolSize = 3,
    invocationCount = 50,
    timeOut = 60000
  )
  public void find(
    final String query,
    final String returnAttrs,
    final String saslAuthz,
    final LdapEntry results)
    throws Exception
  {
    final Query q = new Query();
    q.setLdapQuery(query);
    q.setQueryAttributes(returnAttrs.split("\\|"));
    q.setSaslAuthorizationId(saslAuthz);

    final Iterator<SearchResult> iter = this.searchInvoker.find(q);
    AssertJUnit.assertEquals(
      results,
      TestUtil.convertLdifToEntry((new Ldif()).createLdif(iter)));
  }
}
