/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.x509;

import javax.security.auth.x500.X500Principal;

/**
 * Strategy pattern interface for producing a string representation of an X.500
 * distinguished name.
 *
 * @author  Middleware Services
 * @version  $Revision$
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
