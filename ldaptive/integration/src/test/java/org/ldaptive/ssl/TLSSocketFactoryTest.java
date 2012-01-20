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
package org.ldaptive.ssl;

import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapException;
import org.testng.AssertJUnit;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link TLSSocketFactory}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TLSSocketFactoryTest
{

  /** List of ciphers. */
  public static final String[] CIPHERS = new String[] {
    "TLS_DH_anon_WITH_AES_128_CBC_SHA",
    "TLS_DH_anon_WITH_AES_256_CBC_SHA",
    "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",
    "SSL_DH_anon_WITH_RC4_128_MD5",
    "TLS_RSA_WITH_AES_128_CBC_SHA",
    "TLS_RSA_WITH_AES_256_CBC_SHA",
    "SSL_RSA_WITH_3DES_EDE_CBC_SHA",
    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
    "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
    "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
    "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
    "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
    "SSL_RSA_WITH_RC4_128_MD5",
    "SSL_RSA_WITH_RC4_128_SHA",
  };

  /** List of ciphers. */
  public static final String[] UNKNOWN_CIPHERS = new String[] {
    "TLS_DH_anon_WITH_AES_128_CBC_SHA",
    "TLS_DH_anon_WITH_3DES_256_CBC_SHA",
    "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",
    "SSL_DH_anon_WITH_RC4_128_MD5",
  };

  /** List of protocols. */
  public static final String[] ALL_PROTOCOLS = new String[] {
    "SSLv2Hello",
    "SSLv3",
    "TLSv1",
  };

  /** List of protocols. */
  public static final String[] PROTOCOLS = new String[] {
    "SSLv3",
    "TLSv1",
  };

  /** List of protocols. */
  public static final String[] FAIL_PROTOCOLS = new String[] {
    "SSLv2Hello",
  };

  /** List of protocols. */
  public static final String[] UNKNOWN_PROTOCOLS = new String[] {
    "SSLv3Hello",
    "SSLv2Hello",
    "TLSv1",
  };


  /**
   * @return  context initializer
   *
   * @throws  Exception  if trust material cannot be read.
   */
  public X509SSLContextInitializer createX509SSLContextInitializer()
    throws Exception
  {
    final X509CertificatesCredentialReader reader =
      new X509CertificatesCredentialReader();
    final X509SSLContextInitializer ctxInit =
      new X509SSLContextInitializer();
    ctxInit.setTrustCertificates(
      reader.read("file:target/test-classes/ldaptive.trust.crt"));
    return ctxInit;
  }


  /**
   * @param  url  to connect to
   *
   * @return  connection configuration
   *
   * @throws  Exception  On connection failure.
   */
  public ConnectionConfig createTLSConnectionConfig(final String url)
    throws Exception
  {
    final TLSSocketFactory sf = new TLSSocketFactory();
    sf.setSSLContextInitializer(createX509SSLContextInitializer());
    sf.initialize();

    final ConnectionConfig cc = new ConnectionConfig(url);
    cc.setUseStartTLS(true);
    cc.setSslSocketFactory(sf);
    return cc;
  }


  /**
   * @param  url  to connect to
   *
   * @return  connection configuration
   *
   * @throws  Exception  On connection failure.
   */
  public ConnectionConfig createSSLConnectionConfig(final String url)
    throws Exception
  {
    final SingletonTLSSocketFactory sf = new SingletonTLSSocketFactory();
    sf.setSSLContextInitializer(createX509SSLContextInitializer());
    sf.initialize();

    final ConnectionConfig cc = new ConnectionConfig(url);
    cc.setUseSSL(true);
    cc.setSslSocketFactory(sf);
    return cc;
  }


  /**
   * @param  url  to connect to
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "ldapTestHost" })
  @Test(groups = {"ssl"})
  public void connectTLS(final String url)
    throws Exception
  {
    // with no trusted certificates, connection should fail
    final ConnectionConfig cc = createTLSConnectionConfig(url);
    cc.setSslSocketFactory(null);
    Connection conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.fail("Should have thrown Exception, no exception thrown");
    } catch (Exception e) {
      AssertJUnit.assertNotNull(e);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  url  to connect to
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "ldapSslTestHost" })
  @Test(groups = {"ssl"})
  public void connectSSL(final String url)
    throws Exception
  {
    // with no trusted certificates, connection should fail
    final ConnectionConfig cc = createSSLConnectionConfig(url);
    cc.setSslSocketFactory(null);
    Connection conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.fail("Should have thrown Exception, no exception thrown");
    } catch (Exception e) {
      AssertJUnit.assertNotNull(e);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  url  to connect to
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "ldapTestHost" })
  @Test(groups = {"ssl"})
  public void setEnabledCipherSuites(final String url)
    throws Exception
  {
    final ConnectionConfig cc = createTLSConnectionConfig(url);
    final TLSSocketFactory sf = (TLSSocketFactory) cc.getSslSocketFactory();
    Connection conn = DefaultConnectionFactory.getConnection(cc);

    try {
      conn.open();
      AssertJUnit.assertEquals(
        Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledCipherSuites()),
        Arrays.asList(sf.getDefaultCipherSuites()));
      AssertJUnit.assertNotSame(
        Arrays.asList(sf.getDefaultCipherSuites()), Arrays.asList(CIPHERS));
    } finally {
      conn.close();
    }

    sf.setEnabledCipherSuites(UNKNOWN_CIPHERS);
    conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.fail("Should have thrown Exception, no exception thrown");
    } catch (Exception e) {
      AssertJUnit.assertNotNull(e);
    }

    sf.setEnabledCipherSuites(CIPHERS);
    conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.assertEquals(
        Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledCipherSuites()),
        Arrays.asList(CIPHERS));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  url  to connect to
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "ldapTestHost" })
  @Test(groups = {"ssl"})
  public void setEnabledProtocols(final String url)
    throws Exception
  {
    final ConnectionConfig cc = createTLSConnectionConfig(url);
    final TLSSocketFactory sf = (TLSSocketFactory) cc.getSslSocketFactory();
    Connection conn = DefaultConnectionFactory.getConnection(cc);

    try {
      conn.open();
      AssertJUnit.assertEquals(
        Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledProtocols()),
        Arrays.asList(ALL_PROTOCOLS));
      AssertJUnit.assertNotSame(
        Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledProtocols()),
        Arrays.asList(PROTOCOLS));
    } finally {
      conn.close();
    }

    sf.setEnabledProtocols(FAIL_PROTOCOLS);
    conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.fail("Should have thrown Exception, no exception thrown");
    } catch (Exception e) {
      AssertJUnit.assertNotNull(e);
    }

    sf.setEnabledProtocols(UNKNOWN_PROTOCOLS);
    conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.fail("Should have thrown Exception, no exception thrown");
    } catch (Exception e) {
      AssertJUnit.assertNotNull(e);
    }

    sf.setEnabledProtocols(PROTOCOLS);
    conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.assertEquals(
        Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledProtocols()),
        Arrays.asList(PROTOCOLS));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  url  to connect to
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "ldapSslTestHost" })
  @Test(groups = {"ssl"})
  public void setHostnameVerifier(final String url)
    throws Exception
  {
    final ConnectionConfig cc = createSSLConnectionConfig(url);
    final SingletonTLSSocketFactory sf =
      (SingletonTLSSocketFactory) cc.getSslSocketFactory();
    final HostnameVerifier existingVerifier = sf.getHostnameVerifier();
    
    sf.setHostnameVerifier(new AnyHostnameVerifier());
    sf.initialize();
    Connection conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
    } finally {
      conn.close();
    }

    sf.setHostnameVerifier(new NoHostnameVerifier());
    sf.initialize();
    conn = DefaultConnectionFactory.getConnection(cc);
    try {
      conn.open();
      AssertJUnit.fail("Should have thrown SSLPeerUnverifiedException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(
        SSLPeerUnverifiedException.class, e.getCause().getCause().getClass());
    } finally {
      conn.close();
    }

    sf.setHostnameVerifier(existingVerifier);
  }
}
