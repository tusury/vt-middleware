/*
  $Id$

  Copyright (C) 2007-2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.crypt.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import edu.vt.middleware.crypt.CryptException;

/**
 * Reads a security credential (e.g. key, certificate) from a resource.
 *
 * @param  <T>  Type of credential to read.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface CredentialReader<T>
{

  /**
   * Reads a credential, commonly in encoded format, from the given file.
   *
   * @param  file  File from which to read credential.
   *
   * @return  Credential read from file.
   *
   * @throws  IOException  On IO exceptions.
   * @throws  CryptException  On cryptography errors such as invalid formats,
   * unsupported ciphers, illegal settings.
   */
  T read(File file)
    throws IOException, CryptException;


  /**
   * Reads a credential, commonly in encoded format, from the given input
   * stream.
   *
   * @param  in  Input stream from which to read credential.
   *
   * @return  Credential read from input stream.
   *
   * @throws  IOException  On IO exceptions.
   * @throws  CryptException  On cryptography errors such as invalid formats,
   * unsupported ciphers, illegal settings.
   */
  T read(InputStream in)
    throws IOException, CryptException;
}
