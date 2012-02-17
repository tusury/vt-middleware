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
package org.ldaptive.io;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import org.ldaptive.LdapUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link CertificateValueDecoder} and
 * {@link CertificateValueEncoder}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CertificateValueTest
{

  /** Certificate with CN=a.foo.com. */
  private static final String A_FOO_COM_CERT =
    "MIIDrzCCApegAwIBAgIJAK+nL4I3GkjeMA0GCSqGSIb3DQEBBQUAMEMxEjAQBgNV" +
    "BAMTCWEuZm9vLmNvbTEYMBYGCgmSJomT8ixkARkWCGxkYXB0aXZlMRMwEQYKCZIm" +
    "iZPyLGQBGRYDb3JnMB4XDTEyMDExNzIxNDAxNVoXDTIyMDExNDIxNDAxNVowQzES" +
    "MBAGA1UEAxMJYS5mb28uY29tMRgwFgYKCZImiZPyLGQBGRYIbGRhcHRpdmUxEzAR" +
    "BgoJkiaJk/IsZAEZFgNvcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
    "AQDGRxBVvGZqHFWbYdbpOZaBf4H68b7zjiqbpXXq+mTfVOehIUeyL0624JsdmHLx" +
    "oMNC7K9hAaM88wxcyhBRLRfo4ar1DJspzcFUoz2kFD7ytWGS2zwvV+VWnoXpPNiw" +
    "9QuK6bdA/UYLIg/fk3TwshuoIT9VBJ4L3TRdOYYgH6WJBerQ2L5vMu91B9nBhNqR" +
    "4RG8VFqwgwW9IoXBXC8XTZS5jd0bVoEoeA+PWVENQ3my5ilP4VUqo9h/jPdb8dFW" +
    "3TNoaVHjOiTUOIpH+5cUmi0OkH2NzhTaWmCVoWuzFpvvB6PFHHxut2pDe8eGgc4x" +
    "NdEvZDizbfY6JEb/fkwZ+Im9AgMBAAGjgaUwgaIwHQYDVR0OBBYEFPUscUXspD8Z" +
    "LP3b6yybVhXhp5C2MHMGA1UdIwRsMGqAFPUscUXspD8ZLP3b6yybVhXhp5C2oUek" +
    "RTBDMRIwEAYDVQQDEwlhLmZvby5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2" +
    "ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAK+nL4I3GkjeMAwGA1UdEwQFMAMBAf8w" +
    "DQYJKoZIhvcNAQEFBQADggEBALam5DdoM7cyOS2GbiA7QAfZTJkBcVr4Fef9aDWR" +
    "cG3kzbEbu1OXf3lkRW11H7gPLOgZGebSsxsv6YhKgAtz7py3lyH5QNkrN0OGI1ZA" +
    "eXf76eSR4T26pYjxln26xyZUW/dcddQ0nSj9Yl52oFCWj38DqGaxP6hIu3DHGlcE" +
    "PtpM2T4ZjWgrsqxL8N59zMb0Re9V4Xop7KmsLs3ThF3RWwmZdC1ba5LRPK6lKNF5" +
    "CnSl5YzFUMnpzFZtneUhAHeFxrF+RV4f3bHLNs+sWjlmJo0ukCCnOzoiyE4oOJiL" +
    "AhDym4nIfzng6fgYBeLT1Hp/bKHivQP4ef4wgre6r1ztnFA=";

  /** Decoder to test. */
  private CertificateValueDecoder decoder = new CertificateValueDecoder();

  /** Encoder to test. */
  private CertificateValueEncoder encoder = new CertificateValueEncoder();


  /**
   * Certificate test data.
   *
   * @return  ldap attribute values
   *
   * @throws  Exception  if test data cannot be generated
   */
  @DataProvider(name = "certs")
  public Object[][] createCerts()
    throws Exception
  {
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    final Certificate cert = cf.generateCertificate(
      new ByteArrayInputStream(LdapUtil.base64Decode(A_FOO_COM_CERT)));

    return new Object[][] {
      new Object[] {
        cert,
        "-----BEGIN CERTIFICATE-----\n" +
          A_FOO_COM_CERT +
          "\n-----END CERTIFICATE-----",
        LdapUtil.base64Decode(A_FOO_COM_CERT), },
    };
  }


  /**
   * @param  cert  certificate to compare
   * @param  s  ldap attribute string value
   * @param  b  ldap attribute binary value
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"io"}, dataProvider = "certs")
  public void testDecode(
    final Certificate cert, final String s, final byte[] b)
    throws Exception
  {
    Assert.assertEquals(cert, decoder.decodeStringValue(s));
    Assert.assertEquals(cert, decoder.decodeBinaryValue(b));
  }


  /**
   * @param  cert  certificate to compare
   * @param  s  ldap attribute string value
   * @param  b  ldap attribute binary value
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"io"}, dataProvider = "certs")
  public void testEncode(
    final Certificate cert, final String s, final byte[] b)
    throws Exception
  {
    Assert.assertEquals(s, encoder.encodeStringValue(cert));
    Assert.assertEquals(b, encoder.encodeBinaryValue(cert));
  }
}
