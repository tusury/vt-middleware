/*
  $Id: Base64Converter.java 170 2009-05-05 02:42:59Z dfisher $

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 170 $
  Updated: $Date: 2009-05-04 22:42:59 -0400 (Mon, 04 May 2009) $
*/
package edu.vt.middleware.crypt.util;

import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Encoder;

/**
 * Converts bytes to base-64 encoded strings and vice versa.
 *
 * @author  Middleware Services
 * @version  $Revision: 170 $
 */
public class Base64Converter extends AbstractEncodingConverter
{

  /** Does encoding work. */
  private Base64Encoder encoder = new Base64Encoder();

  /** {@inheritDoc} */
  protected Encoder getEncoder()
  {
    return encoder;
  }
}
