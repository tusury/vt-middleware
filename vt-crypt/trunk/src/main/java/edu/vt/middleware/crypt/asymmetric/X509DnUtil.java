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
package edu.vt.middleware.crypt.asymmetric;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p><code>X509DnUtil</code> provides methods for working with X.509
 * distinguished names.</p>
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public final class X509DnUtil
{

  /**
   * <p>Default constructor.</p>
   */
  private X509DnUtil() {}


  /**
   * <p>This returns the common name in the supplied certificate's DN. If no CN
   * exists, then null is returned.</p>
   *
   * @param  certificate  <code>X509Certificate</code>
   *
   * @return  <code>String</code> - common name
   */
  public static String getSubjectCN(final X509Certificate certificate)
  {
    final String[] cn = getSubjectDNAttributes(certificate, "CN");
    return cn[0];
  }


  /**
   * <p>This returns an array of the requested attributes of the supplied
   * certificate's DN.</p>
   *
   * @param  certificate  <code>X509Certificate</code>
   * @param  attribute  <code>String</code>
   *
   * @return  <code>String[]</code> - of attributes
   */
  public static String[] getSubjectDNAttributes(
    final X509Certificate certificate,
    final String attribute)
  {
    final List results = new ArrayList();
    final StringBuffer newAttribute = new StringBuffer(attribute.toLowerCase());
    newAttribute.append("=");

    final String dn = certificate.getSubjectDN().getName();
    if (dn != null) {
      final StringTokenizer attrs = new StringTokenizer(dn, ",");
      while (attrs.hasMoreTokens()) {
        final String attr = attrs.nextToken().trim();
        if (attr.toLowerCase().startsWith(newAttribute.toString())) {
          final StringTokenizer attrComponent = new StringTokenizer(attr, "=");
          attrComponent.nextToken();
          if (attrComponent.hasMoreTokens()) {
            results.add(attrComponent.nextToken());
          }
        }
      }
    }
    return (String[]) results.toArray(new String[0]);
  }
}
