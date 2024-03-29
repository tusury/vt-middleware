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
package org.ldaptive.auth;

import java.util.HashMap;
import java.util.Map;
import org.ldaptive.AbstractTest;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactoryManager;
import org.ldaptive.Credential;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapEntry;
import org.ldaptive.TestControl;
import org.ldaptive.TestUtils;
import org.ldaptive.ad.extended.FastBindOperation;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.PooledConnectionFactory;
import org.ldaptive.pool.PooledConnectionFactoryManager;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Load test for {@link Authenticator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthenticatorLoadTest extends AbstractTest
{

  /** Invalid password test data. */
  public static final String INVALID_PASSWD = "not-a-password";

  /** Invalid filter test data. */
  public static final String INVALID_FILTER = "(departmentNumber=1111)";

  /** Entries for auth tests. */
  private static Map<String, LdapEntry[]> entries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of entries.
   */
  static {
    for (int i = 2; i <= 10; i++) {
      entries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleTLSAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator pooledTLSAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleADFastBind;

  /** Authenticator instance for concurrency testing. */
  private Authenticator pooledADFastBind;


  /**
   * Default constructor.
   *
   * @throws  Exception  if ldap cannot be constructed
   */
  public AuthenticatorLoadTest()
    throws Exception
  {
    singleTLSAuth = TestUtils.readAuthenticator(
      "classpath:/org/ldaptive/ldap.tls.load.properties");
    pooledTLSAuth = TestUtils.readAuthenticator(
      "classpath:/org/ldaptive/ldap.tls.load.properties");
    singleADFastBind = TestUtils.readAuthenticator(
      "classpath:/org/ldaptive/ldap.tls.load.properties");
    pooledADFastBind = TestUtils.readAuthenticator(
      "classpath:/org/ldaptive/ldap.tls.load.properties");
  }


  /**
   * @param  ldifFile2  to create.
   * @param  ldifFile3  to create.
   * @param  ldifFile4  to create.
   * @param  ldifFile5  to create.
   * @param  ldifFile6  to create.
   * @param  ldifFile7  to create.
   * @param  ldifFile8  to create.
   * @param  ldifFile9  to create.
   * @param  ldifFile10  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "createEntry2",
      "createEntry3",
      "createEntry4",
      "createEntry5",
      "createEntry6",
      "createEntry7",
      "createEntry8",
      "createEntry9",
      "createEntry10"
    }
  )
  @BeforeClass(groups = {"authload"})
  public void createAuthEntry(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4,
    final String ldifFile5,
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9,
    final String ldifFile10)
    throws Exception
  {
    // initialize the pooled authenticator
    DefaultConnectionFactory drcf =
      (DefaultConnectionFactory)
        ((ConnectionFactoryManager)
          pooledTLSAuth.getDnResolver()).getConnectionFactory();
    BlockingConnectionPool resolverCp = new BlockingConnectionPool(drcf);
    resolverCp.initialize();
    PooledConnectionFactory drFactory = new PooledConnectionFactory(resolverCp);
    PooledSearchDnResolver dr = new PooledSearchDnResolver(drFactory);
    dr.setBaseDn(
      ((SearchDnResolver) pooledTLSAuth.getDnResolver()).getBaseDn());
    dr.setUserFilter(
      ((SearchDnResolver) pooledTLSAuth.getDnResolver()).getUserFilter());
    pooledTLSAuth.setDnResolver(dr);

    DefaultConnectionFactory ahcf =
      (DefaultConnectionFactory)
        ((ConnectionFactoryManager)
          pooledTLSAuth.getAuthenticationHandler()).getConnectionFactory();
    ConnectionConfig ahcc = ConnectionConfig.newConnectionConfig(
      ahcf.getConnectionConfig());
    ahcc.setConnectionInitializer(null);
    ahcf.setConnectionConfig(ahcc);
    BlockingConnectionPool ahCp = new BlockingConnectionPool(ahcf);
    ahCp.initialize();
    PooledConnectionFactory ahFactory = new PooledConnectionFactory(ahCp);
    pooledTLSAuth.setAuthenticationHandler(
      new PooledBindAuthenticationHandler(ahFactory));

    // initialize the ad authenticator
    if (TestControl.isActiveDirectory()) {
      ahcf =
        (DefaultConnectionFactory)
          ((ConnectionFactoryManager)
            singleADFastBind.getAuthenticationHandler()).getConnectionFactory();
      ahcc = ConnectionConfig.newConnectionConfig(ahcf.getConnectionConfig());
      ahcc.setConnectionInitializer(
        new FastBindOperation.FastBindConnectionInitializer());
      ((BindAuthenticationHandler)
        singleADFastBind.getAuthenticationHandler()).setConnectionFactory(
          new DefaultConnectionFactory(ahcc));
      // initialize the pooled ad authenticator
      drcf =
        (DefaultConnectionFactory)
          ((SearchDnResolver)
            pooledADFastBind.getDnResolver()).getConnectionFactory();
      resolverCp = new BlockingConnectionPool(drcf);
      resolverCp.initialize();
      drFactory = new PooledConnectionFactory(resolverCp);
      dr = new PooledSearchDnResolver(drFactory);
      dr.setBaseDn(
        ((SearchDnResolver) pooledADFastBind.getDnResolver()).getBaseDn());
      dr.setUserFilter(
        ((SearchDnResolver) pooledADFastBind.getDnResolver()).getUserFilter());
      pooledADFastBind.setDnResolver(dr);

      ahcf =
        (DefaultConnectionFactory)
          ((ConnectionFactoryManager)
            pooledADFastBind.getAuthenticationHandler()).getConnectionFactory();
      ahcc = ConnectionConfig.newConnectionConfig(ahcf.getConnectionConfig());
      ahcc.setConnectionInitializer(
        new FastBindOperation.FastBindConnectionInitializer());
      ahCp = new BlockingConnectionPool(new DefaultConnectionFactory(ahcc));
      ahCp.initialize();
      ahFactory = new PooledConnectionFactory(ahCp);
      pooledADFastBind.setAuthenticationHandler(
        new PooledBindAuthenticationHandler(ahFactory));
    }

    // CheckStyle:Indentation OFF
    entries.get("2")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile2)).getEntry();
    entries.get("3")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile3)).getEntry();
    entries.get("4")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile4)).getEntry();
    entries.get("5")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile5)).getEntry();
    entries.get("6")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile6)).getEntry();
    entries.get("7")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile7)).getEntry();
    entries.get("8")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile8)).getEntry();
    entries.get("9")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile9)).getEntry();
    entries.get("10")[0] = TestUtils.convertLdifToResult(
      TestUtils.readFileIntoString(ldifFile10)).getEntry();
    // CheckStyle:Indentation ON

    for (Map.Entry<String, LdapEntry[]> e : entries.entrySet()) {
      super.createLdapEntry(e.getValue()[0]);
    }
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"authload"})
  public void deleteAuthEntry()
    throws Exception
  {
    super.deleteLdapEntry(entries.get("2")[0].getDn());
    super.deleteLdapEntry(entries.get("3")[0].getDn());
    super.deleteLdapEntry(entries.get("4")[0].getDn());
    super.deleteLdapEntry(entries.get("5")[0].getDn());
    super.deleteLdapEntry(entries.get("6")[0].getDn());
    super.deleteLdapEntry(entries.get("7")[0].getDn());
    super.deleteLdapEntry(entries.get("8")[0].getDn());
    super.deleteLdapEntry(entries.get("9")[0].getDn());
    super.deleteLdapEntry(entries.get("10")[0].getDn());

    final DnResolver dr = pooledTLSAuth.getDnResolver();
    ((PooledConnectionFactoryManager)
      dr).getConnectionFactory().getConnectionPool().close();
    final AuthenticationHandler ah = pooledTLSAuth.getAuthenticationHandler();
    ((PooledConnectionFactoryManager)
      ah).getConnectionFactory().getConnectionPool().close();
  }


  /**
   * Sample authentication data.
   *
   * @return  user authentication data
   */
  @DataProvider(name = "auth-data")
  public Object[][] createAuthData()
  {
    return
      new Object[][] {
        {
          "jadams@ldaptive.org",
          "password2",
          "cn",
          "cn=John Adams",
        },
        {
          "tjefferson@ldaptive.org",
          "password3",
          "givenName|sn",
          "givenName=Thomas|sn=Jefferson",
        },
        {
          "jmadison@ldaptive.org",
          "password4",
          "givenName|sn",
          "givenName=James|sn=Madison",
        },
        {
          "jmonroe@ldaptive.org",
          "password5",
          "givenName|sn",
          "givenName=James|sn=Monroe",
        },
        {
          "jqadams@ldaptive.org",
          "password6",
          "cn",
          "cn=John Quincy Adams",
        },
        {
          "ajackson@ldaptive.org",
          "password7",
          "givenName|sn",
          "givenName=Andrew|sn=Jackson",
        },
        {
          "mvburen@ldaptive.org",
          "password8",
          "givenName|sn",
          "givenName=Martin|sn=Buren",
        },
        {
          "whharrison@ldaptive.org",
          "password9",
          "givenName|sn",
          "givenName=William|sn=Harrison",
        },
        {
          "jtyler@ldaptive.org",
          "password10",
          "givenName|sn",
          "givenName=John|sn=Tyler",
        },
      };
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  expectedAttrs  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"authload"},
    dataProvider = "auth-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000
  )
  public void authenticate(
    final String user,
    final String credential,
    final String returnAttrs,
    final String expectedAttrs)
    throws Exception
  {
    // test auth with return attributes
    final LdapEntry expected = TestUtils.convertStringToEntry(
      null, expectedAttrs);
    final AuthenticationResponse response = singleTLSAuth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), returnAttrs.split("\\|")));
    expected.setDn(response.getLdapEntry().getDn());
    TestUtils.assertEquals(expected, response.getLdapEntry());
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  expectedAttrs  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"authload"},
    dataProvider = "auth-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000
  )
  public void authenticatePooled(
    final String user,
    final String credential,
    final String returnAttrs,
    final String expectedAttrs)
    throws Exception
  {
    // test auth with return attributes
    final LdapEntry expected = TestUtils.convertStringToEntry(
      null, expectedAttrs);
    final AuthenticationResponse response = pooledTLSAuth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), returnAttrs.split("\\|")));
    expected.setDn(response.getLdapEntry().getDn());
    TestUtils.assertEquals(expected, response.getLdapEntry());
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  expectedAttrs  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"authload"},
    dataProvider = "auth-data",
    threadPoolSize = 5,
    invocationCount = 50,
    timeOut = 60000
  )
  public void authenticateADFastBind(
    final String user,
    final String credential,
    final String returnAttrs,
    final String expectedAttrs)
    throws Exception
  {
    if (!TestControl.isActiveDirectory()) {
      return;
    }

    // test auth with fast bind
    final AuthenticationResponse response = singleADFastBind.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  expectedAttrs  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"authload"},
    dataProvider = "auth-data",
    threadPoolSize = 5,
    invocationCount = 50,
    timeOut = 60000
  )
  public void authenticatePooledADFastBind(
    final String user,
    final String credential,
    final String returnAttrs,
    final String expectedAttrs)
    throws Exception
  {
    if (!TestControl.isActiveDirectory()) {
      return;
    }

    // test auth with return attributes
    final LdapEntry expected = TestUtils.convertStringToEntry(
      null, expectedAttrs);
    final AuthenticationResponse response = pooledADFastBind.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), returnAttrs.split("\\|")));
    AssertJUnit.assertTrue(response.getResult());
  }
}
