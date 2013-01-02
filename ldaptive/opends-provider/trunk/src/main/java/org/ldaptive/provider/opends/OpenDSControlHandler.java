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
package org.ldaptive.provider.opends;

import org.ldaptive.control.ControlFactory;
import org.ldaptive.control.RequestControl;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.ControlHandler;
import org.opends.sdk.ByteStringBuilder;
import org.opends.sdk.controls.GenericControl;

/**
 * OpenDS control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDSControlHandler
  implements ControlHandler<org.opends.sdk.controls.Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID(final org.opends.sdk.controls.Control control)
  {
    return control.getOID();
  }


  /** {@inheritDoc} */
  @Override
  public org.opends.sdk.controls.Control processRequest(
    final RequestControl requestControl)
  {
    final byte[] value = requestControl.encode();
    if (value == null) {
      return
        GenericControl.newControl(
          requestControl.getOID(),
          requestControl.getCriticality());
    } else {
      final ByteStringBuilder builder = new ByteStringBuilder(value.length);
      builder.append(value);
      return
        GenericControl.newControl(
          requestControl.getOID(),
          requestControl.getCriticality(),
          builder.toByteString());
    }
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl processResponse(
    final org.opends.sdk.controls.Control responseControl)
  {
    return
      ControlFactory.createResponseControl(
        responseControl.getOID(),
        responseControl.isCritical(),
        responseControl.getValue().toByteArray());
  }
}
