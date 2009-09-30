/*
  $Id: DirectByteArrayOutputStream.java 3 2008-11-11 20:58:48Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 3 $
  Updated: $Date: 2008-11-11 15:58:48 -0500 (Tue, 11 Nov 2008) $
*/
package edu.vt.middleware.crypt.io;

import java.io.ByteArrayOutputStream;

/**
 * Extends {@link java.io.ByteArrayOutputStream} by allowing direct access to
 * the internal byte buffer.
 *
 * @author  Middleware Services
 * @version  $Revision: 3 $
 */
public class DirectByteArrayOutputStream extends ByteArrayOutputStream
{

  /** Creates a new instance with a buffer of the default size. */
  public DirectByteArrayOutputStream()
  {
    super();
  }


  /**
   * Creates a new instance with a buffer of the given initial capacity.
   *
   * @param  capacity  Initial capacity of internal buffer.
   */
  public DirectByteArrayOutputStream(final int capacity)
  {
    super(capacity);
  }


  /**
   * Gets the internal byte buffer.
   *
   * @return  Internal buffer that holds written bytes.
   */
  public byte[] getBuffer()
  {
    return buf;
  }
}
