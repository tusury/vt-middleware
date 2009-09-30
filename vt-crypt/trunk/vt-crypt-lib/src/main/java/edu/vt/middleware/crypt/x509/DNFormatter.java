/*
  $Id: DNFormatter.java 578 2009-09-08 19:10:23Z marvin.addison $

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

/**
 * Strategy pattern interface for producing a string representation of an X.500
 * distinguished name.
 *
 * @author Middleware
 * @version $Revision: 578 $
 *
 */
public interface DNFormatter
{
  /**
   * Produces a string representation of the given X.500 principal.
   *
   * @param  dn  Distinguished name as as X.500 principal.
   *
   * @return  String representation of DN.
   */
  String format(X500Principal dn);
}
