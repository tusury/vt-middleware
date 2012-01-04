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
package org.ldaptive.provider.netscape;

import org.ldaptive.control.ControlFactory;
import org.ldaptive.control.RequestControl;
import org.ldaptive.control.ResponseControl;
import org.ldaptive.provider.ControlHandler;

/**
 * Netscape control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeControlHandler
  implements ControlHandler<netscape.ldap.LDAPControl>
{


  /** {@inheritDoc} */
  @Override
  public String getOID(final netscape.ldap.LDAPControl control)
  {
    return control.getID();
  }


  /** {@inheritDoc} */
  @Override
  public netscape.ldap.LDAPControl processRequest(
    final RequestControl requestControl)
  {
    return
      new netscape.ldap.LDAPControl(
        requestControl.getOID(),
        requestControl.getCriticality(),
        requestControl.encode());
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl processResponse(
    final netscape.ldap.LDAPControl responseControl)
  {
    return
      ControlFactory.createResponseControl(
        responseControl.getID(),
        responseControl.isCritical(),
        responseControl.getValue());
  }
}
