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
package org.ldaptive.ssl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import org.ldaptive.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all credential readers. It provides support for loading files
 * from resources on the classpath or a filepath. If a path is prefixed with the
 * string "classpath:" it is interpreted as a classpath specification. If a path
 * is prefixed with the string "file:" it is interpreted as a file path. Any
 * other input throws IllegalArgumentException.
 *
 * @param  <T>  Type of credential read by this instance.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractCredentialReader<T> implements CredentialReader<T>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public T read(final String path, final String... params)
    throws IOException, GeneralSecurityException
  {
    final InputStream is = LdapUtils.getResource(path);
    if (is != null) {
      try {
        return read(is, params);
      } finally {
        logger.debug("Successfully loaded {}", path);
        is.close();
      }
    } else {
      logger.debug("Failed to load {}", path);
      return null;
    }
  }


  /**
   * Gets a buffered input stream from the given input stream. If the given
   * instance is already buffered, it is simply returned.
   *
   * @param  is  input stream from which to create buffered instance.
   *
   * @return  buffered input stream. If the given instance is already buffered,
   * it is simply returned.
   */
  protected InputStream getBufferedInputStream(final InputStream is)
  {
    if (is.markSupported()) {
      return is;
    } else {
      return new BufferedInputStream(is);
    }
  }
}
