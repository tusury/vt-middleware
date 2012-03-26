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

import org.ldaptive.AbstractTest;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionFactoryManager;
import org.ldaptive.Credential;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.LdapResult;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.ReferralBehavior;
import org.ldaptive.ResultCode;
import org.ldaptive.TestUtils;
import org.ldaptive.auth.ext.PasswordPolicyAuthenticationResponseHandler;
import org.ldaptive.control.PasswordPolicyControl;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.PooledConnectionFactory;
import org.ldaptive.pool.PooledConnectionFactoryManager;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Authenticator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthenticatorTest extends AbstractTest
{

  /** Invalid password test data. */
  public static final String INVALID_PASSWD = "not-a-password";

  /** Invalid filter test data. */
  public static final String INVALID_FILTER = "(departmentNumber=1111)";

  /** Entry created for auth tests. */
  private static LdapEntry testLdapEntry;

  /** Entry created for auth tests. */
  private static LdapEntry specialCharsLdapEntry;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleTLSAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleSSLAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleTLSDnAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleSSLDnAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator pooledTLSAuth;

  /**
   * Default constructor.
   *
   * @throws  Exception  if ldap cannot be constructed
   */
  public AuthenticatorTest()
    throws Exception
  {
    singleTLSAuth = TestUtils.createTLSAuthenticator();
    singleSSLAuth = TestUtils.createSSLAuthenticator();
    singleTLSDnAuth = TestUtils.createTLSDnAuthenticator();
    singleSSLDnAuth = TestUtils.createSSLDnAuthenticator();
    pooledTLSAuth = TestUtils.createTLSAuthenticator();
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry6")
  @BeforeClass(groups = {"auth"})
  public void createAuthEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtils.readFileIntoString(ldifFile);
    testLdapEntry = TestUtils.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);

    final AuthenticationHandler ah = pooledTLSAuth.getAuthenticationHandler();
    final DefaultConnectionFactory cf =
      (DefaultConnectionFactory)
        ((ConnectionFactoryManager) ah).getConnectionFactory();
    final BlockingConnectionPool cp = new BlockingConnectionPool(cf);
    final PooledConnectionFactory pcf = new PooledConnectionFactory(cp);
    pooledTLSAuth.setAuthenticationHandler(
      new PooledBindAuthenticationHandler(pcf));
    try {
      cp.initialize();
    } catch (UnsupportedOperationException e) {
      // ignore if not supported
      AssertJUnit.assertNotNull(e);
    }
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createSpecialCharsEntry2")
  @BeforeClass(groups = {"auth"})
  public void createSpecialCharsEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtils.readFileIntoString(ldifFile);
    specialCharsLdapEntry = TestUtils.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(specialCharsLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"auth"})
  public void deleteAuthEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
    super.deleteLdapEntry(specialCharsLdapEntry.getDn());
    final AuthenticationHandler ah = pooledTLSAuth.getAuthenticationHandler();
    (((PooledConnectionFactoryManager)
      ah).getConnectionFactory().getConnectionPool()).close();
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createTLSAuthenticator(
    final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtils.createTLSAuthenticator();
    }
    return singleTLSAuth;
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createTLSDnAuthenticator(
    final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtils.createTLSDnAuthenticator();
    }
    return singleTLSDnAuth;
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createSSLAuthenticator(
    final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtils.createSSLAuthenticator();
    }
    return singleSSLAuth;
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createSSLDnAuthenticator(
    final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtils.createSSLDnAuthenticator();
    }
    return singleSSLDnAuth;
  }


  /**
   * @param  ldapUrl  to check
   * @param  baseDn  to check
   */
  @Parameters({ "loadPropertiesUrl", "loadPropertiesBaseDn" })
  @Test(groups = {"auth"})
  public void loadProperties(final String ldapUrl, final String baseDn)
  {
    final Authenticator auth = TestUtils.readAuthenticator(
      "classpath:/org/ldaptive/ldap.tls.properties");
    final SearchDnResolver dnResolver = (SearchDnResolver) auth.getDnResolver();
    final DefaultConnectionFactory resolverCf =
      (DefaultConnectionFactory) dnResolver.getConnectionFactory();
    AssertJUnit.assertEquals(
      ldapUrl,
      resolverCf.getConnectionConfig().getLdapUrl());
    AssertJUnit.assertEquals(
      baseDn, ((SearchDnResolver) auth.getDnResolver()).getBaseDn());
  }


  /**
   * @param  uid  to get dn for.
   * @param  user  to get dn for.
   * @param  duplicateFilter  for user lookups
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "getDnUid", "getDnUser", "getDnDuplicateFilter" })
  @Test(groups = {"auth"})
  public void resolveDn(
    final String uid,
    final String user,
    final String duplicateFilter)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(true);

    // test input
    AssertJUnit.assertNull(auth.resolveDn(null));
    AssertJUnit.assertNull(auth.resolveDn(""));

    final SearchDnResolver resolver = (SearchDnResolver) auth.getDnResolver();

    // test format dn
    auth.setDnResolver(
      new FormatDnResolver("uid=%s,%s", new Object[] {resolver.getBaseDn()}));
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(uid));
    auth.setDnResolver(resolver);

    // test one level searching
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(user));

    // test duplicate DNs
    final String filter = resolver.getUserFilter();
    resolver.setUserFilter(duplicateFilter);
    try {
      auth.resolveDn(user);
      AssertJUnit.fail("Should have thrown LdapException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(LdapException.class, e.getClass());
    }

    resolver.setAllowMultipleDns(true);
    auth.resolveDn(user);
    resolver.setUserFilter(filter);
    resolver.setAllowMultipleDns(false);

    // test subtree searching
    resolver.setSubtreeSearch(true);
    resolver.setReferralBehavior(ReferralBehavior.IGNORE);
    final String baseDn = resolver.getBaseDn();
    resolver.setBaseDn(baseDn.substring(baseDn.indexOf(",") + 1));
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(user));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateDn",
      "authenticateDnCredential",
      "authenticateDnReturnAttrs",
      "authenticateDnResults"
    }
  )
  @Test(
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateDn(
    final String dn,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    final Authenticator auth = createTLSDnAuthenticator(false);
    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(dn, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());

    response = auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());

    // test auth with return attributes
    final String expected = TestUtils.readFileIntoString(ldifFile);
    response = auth.authenticate(
      new AuthenticationRequest(
        dn, new Credential(credential), returnAttrs.split("\\|")));
    AssertJUnit.assertEquals(
      TestUtils.convertLdifToResult(expected),
      new LdapResult(response.getLdapEntry()));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateDn",
      "authenticateDnCredential",
      "authenticateDnReturnAttrs",
      "authenticateDnResults"
    }
  )
  @Test(
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateDnSsl(
    final String dn,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    final Authenticator auth = createSSLDnAuthenticator(false);
    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(dn, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());

    response = auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());

    // test auth with return attributes
    final String expected = TestUtils.readFileIntoString(ldifFile);
    response = auth.authenticate(
      new AuthenticationRequest(
        dn, new Credential(credential), returnAttrs.split("\\|")));
    AssertJUnit.assertEquals(
      TestUtils.convertLdifToResult(expected),
      new LdapResult(response.getLdapEntry()));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  filterParameters  to authorize with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateDn",
      "authenticateDnCredential",
      "authenticateDnFilter",
      "authenticateDnFilterParameters"
    }
  )
  @Test(groups = {"auth"})
  public void authenticateDnHandler(
    final String dn,
    final String credential,
    final String filter,
    final String filterParameters)
    throws Exception
  {
    final Authenticator auth = createTLSDnAuthenticator(true);

    final TestAuthenticationResponseHandler authHandler =
      new TestAuthenticationResponseHandler();
    auth.setAuthenticationResponseHandlers(
      new AuthenticationResponseHandler[] {authHandler});

    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(dn, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());
    AssertJUnit.assertTrue(!authHandler.getResults().isEmpty());
    AssertJUnit.assertFalse(authHandler.getResults().get(dn).booleanValue());

    response = auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());
    AssertJUnit.assertTrue(authHandler.getResults().get(dn).booleanValue());

    authHandler.getResults().clear();

    response = auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());
    AssertJUnit.assertTrue(authHandler.getResults().get(dn).booleanValue());
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "digestMd5User", "digestMd5Credential" })
  @Test(groups = {"auth"})
  public void authenticateDigestMd5(final String user, final String credential)
    throws Exception
  {
    final Authenticator auth = TestUtils.createDigestMD5Authenticator();

    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(INVALID_PASSWD), new String[0]));
    AssertJUnit.assertFalse(response.getResult());

    response = auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), new String[0]));
    AssertJUnit.assertTrue(response.getResult());
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "cramMd5User", "cramMd5Credential" })
  @Test(groups = {"auth"})
  public void authenticateCramMd5(final String user, final String credential)
    throws Exception
  {
    final Authenticator auth = TestUtils.createCramMD5Authenticator();
    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(INVALID_PASSWD), new String[0]));
    AssertJUnit.assertFalse(response.getResult());

    response = auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), new String[0]));
    AssertJUnit.assertTrue(response.getResult());
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateReturnAttrs",
      "authenticateResults"
    }
  )
  @Test(
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticate(
    final String user,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(false);

    // test plain auth
    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());
    AssertJUnit.assertEquals(
      ResultCode.INVALID_CREDENTIALS, response.getResultCode());

    response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());
    AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    AssertJUnit.assertNull(response.getMessage());

    // test auth with return attributes
    final String expected = TestUtils.readFileIntoString(ldifFile);
    response = auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|")));
    AssertJUnit.assertTrue(response.getResult());
    AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    AssertJUnit.assertNull(response.getMessage());
    AssertJUnit.assertEquals(
      TestUtils.convertLdifToResult(expected),
      new LdapResult(response.getLdapEntry()));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateReturnAttrs",
      "authenticateResults"
    }
  )
  @Test(
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateSsl(
    final String user,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = createSSLAuthenticator(false);

    // test plain auth
    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());

    response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());

    // test auth with return attributes
    final String expected = TestUtils.readFileIntoString(ldifFile);
    response = auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|")));
    AssertJUnit.assertEquals(
      TestUtils.convertLdifToResult(expected),
      new LdapResult(response.getLdapEntry()));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateFilter",
      "authenticateReturnAttrs",
      "authenticateResults"
    }
  )
  @Test(
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticatePooled(
    final String user,
    final String credential,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    AuthenticationResponse response = pooledTLSAuth.authenticate(
      new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());

    response = pooledTLSAuth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());

    // test auth with return attributes
    final String expected = TestUtils.readFileIntoString(ldifFile);
    response = pooledTLSAuth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|")));
    AssertJUnit.assertEquals(
      TestUtils.convertLdifToResult(expected),
      new LdapResult(response.getLdapEntry()));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateReturnAttrs"
    }
  )
  @Test(groups = {"auth"})
  public void authenticateExceptions(
    final String user,
    final String credential,
    final String returnAttrs)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(true);

    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, null, returnAttrs.split("\\|")));
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (UnsupportedOperationException e) {
      throw e;
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }

    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(""), returnAttrs.split("\\|")));
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }

    try {
      auth.authenticate(
        new AuthenticationRequest(
          null, new Credential(credential), returnAttrs.split("\\|")));
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }

    try {
      auth.authenticate(
        new AuthenticationRequest(
          "", new Credential(credential), returnAttrs.split("\\|")));
      AssertJUnit.fail("Should have thrown IllegalArgumentException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    }
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateSpecialCharsUser",
      "authenticateSpecialCharsCredential"
    }
  )
  @Test(groups = {"auth"})
  public void authenticateSpecialChars(
    final String user, final String credential)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(true);

    // test without rewrite
    AuthenticationResponse response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());

    response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());

    // test with rewrite
    ((SearchDnResolver) auth.getDnResolver()).setBaseDn("dc=blah");
    ((SearchDnResolver) auth.getDnResolver()).setSubtreeSearch(true);
    ((SearchDnResolver) auth.getDnResolver()).setReferralBehavior(
      ReferralBehavior.IGNORE);
    response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
    AssertJUnit.assertFalse(response.getResult());

    response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    AssertJUnit.assertTrue(response.getResult());
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential"
    }
  )
  @Test(groups = {"auth"})
  public void authenticatePasswordPolicy(
    final String user, final String credential)
    throws Exception
  {
    final PasswordPolicyControl ppc = new PasswordPolicyControl();
    final Connection conn = TestUtils.createSetupConnection();
    AuthenticationResponse response = null;
    PasswordPolicyControl ppcResponse = null;
    final Authenticator auth = createTLSAuthenticator(true);
    auth.setAuthenticationResponseHandlers(
      new AuthenticationResponseHandler[] {
        new PasswordPolicyAuthenticationResponseHandler(), });
    try {
      conn.open();

      final BindAuthenticationHandler ah =
        (BindAuthenticationHandler) auth.getAuthenticationHandler();
      ah.setAuthenticationControls(ppc);

      // test bind sending ppolicy control
      response = auth.authenticate(
        new AuthenticationRequest(user, new Credential(credential)));
      final LdapEntry entry = response.getLdapEntry();

      // test bind on locked account
      final ModifyOperation modify = new ModifyOperation(conn);
      modify.execute(
        new ModifyRequest(
          entry.getDn(),
          new AttributeModification[] {
            new AttributeModification(
              AttributeModificationType.ADD,
              new LdapAttribute("pwdAccountLockedTime", "000001010000Z")), }));

      response = auth.authenticate(
        new AuthenticationRequest(user, new Credential(credential)));
      AssertJUnit.assertFalse(response.getResult());
      ppcResponse = (PasswordPolicyControl) response.getControls()[0];
      AssertJUnit.assertEquals(
        PasswordPolicyControl.Error.ACCOUNT_LOCKED, ppcResponse.getError());
      AssertJUnit.assertEquals(
        PasswordPolicyControl.Error.ACCOUNT_LOCKED.getCode(),
        response.getAccountState().getError().getCode());

      // test bind with expiration time
      modify.execute(
        new ModifyRequest(
          entry.getDn(),
          new AttributeModification[] {
            new AttributeModification(
              AttributeModificationType.REMOVE,
              new LdapAttribute("pwdAccountLockedTime")), }));
    } finally {
      conn.close();
    }

    response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    ppcResponse = (PasswordPolicyControl) response.getControls()[0];
    AssertJUnit.assertTrue(ppcResponse.getTimeBeforeExpiration() > 0);
    AssertJUnit.assertNotNull(
      response.getAccountState().getWarning().getExpiration());
  }
}
