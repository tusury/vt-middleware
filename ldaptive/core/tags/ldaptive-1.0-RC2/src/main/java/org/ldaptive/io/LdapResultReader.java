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

import java.io.IOException;
import org.ldaptive.LdapResult;

/**
 * Interface for reading ldap results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapResultReader
{


  /**
   * Reads an ldap result.
   *
   * @return  ldap result
   *
   * @throws  IOException  if an error occurs using the reader
   */
  LdapResult read()
    throws IOException;
}
