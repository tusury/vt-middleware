/*
  $Id: LDAPv3DNFormatter.java 578 2009-09-08 19:10:23Z marvin.addison $

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision: 578 $
  Updated: $Date: 2009-09-08 15:10:23 -0400 (Tue, 08 Sep 2009) $
*/
package edu.vt.middleware.crypt.x509;

import javax.security.auth.x500.X500Principal;

import edu.vt.middleware.crypt.x509.types.RelativeDistinguishedName;

/**
 * Produces a string representation of an X.500 distinguished name using the
 * process described in section 2 of RFC 2253, LADPv3 Distinguished Names.
 *
 * @author Middleware
 * @version $Revision: 578 $
 *
 */
public class LDAPv3DNFormatter implements DNFormatter
{
  /** Separator character relative distinguished name components */
  public static final char SEPARATOR_CHAR = ',';

  /** {@inheritDoc} */
  public String format(final X500Principal dn)
  {
    final RDNSequenceIterator rdnSeqIter =
      new RDNSequenceIterator(dn.getEncoded());
    final StringBuilder sb = new StringBuilder(300);
    int i = 0;
    for (RelativeDistinguishedName rdn : rdnSeqIter) {
      if (i++ > 0) {
        sb.append(SEPARATOR_CHAR);
      }
      sb.append(rdn.toString());
    }
    return sb.toString();
  }

}
