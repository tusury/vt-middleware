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
package edu.vt.middleware.crypt.util;
//
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;

import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Encoder;

/**
 * Converts bytes to base-64 encoded strings and vice versa.
 *
 * @author  Middleware Services
 * @version  $Revision$
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
