/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.provider;

import javax.naming.ldap.Control;
import org.ldaptive.provider.jndi.JndiControlHandler;

/**
 * Control processor for testing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class TestControlProcessor extends ControlProcessor<Control>
{

  /**
   * Default constructor.
   */
  public TestControlProcessor()
  {
    super(new JndiControlHandler());
  }
}
