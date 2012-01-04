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
package org.ldaptive.provider.apache;

import org.apache.directory.shared.ldap.extras.controls.ppolicy.PasswordPolicy;
import org.apache.directory.shared.ldap.extras.controls.ppolicy.PasswordPolicyErrorEnum;
import org.apache.directory.shared.ldap.extras.controls.ppolicy.PasswordPolicyImpl;
import org.apache.directory.shared.ldap.extras.controls.ppolicy.PasswordPolicyResponse;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaITImpl;
import org.apache.directory.shared.ldap.model.message.controls.PagedResults;
import org.apache.directory.shared.ldap.model.message.controls.PagedResultsImpl;
import org.ldaptive.control.ManageDsaITControl;
import org.ldaptive.control.PagedResultsControl;
import org.ldaptive.control.PasswordPolicyControl;
import org.ldaptive.provider.ControlHandler;

/**
 * Apache Ldap control handler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ApacheLdapControlHandler implements ControlHandler<Control>
{


  /** {@inheritDoc} */
  @Override
  public String getOID(final Control control)
  {
    return control.getOid();
  }


  /** {@inheritDoc} */
  @Override
  public Control processRequest(
    final org.ldaptive.control.RequestControl requestControl)
  {
    Control ctl = null;
    if (ManageDsaITControl.OID.equals(requestControl.getOID())) {
      ctl = new ManageDsaITImpl(requestControl.getCriticality());
    } else if (PagedResultsControl.OID.equals(requestControl.getOID())) {
      final PagedResultsControl c = (PagedResultsControl) requestControl;
      ctl = new PagedResultsImpl();
      ((PagedResultsImpl) ctl).setSize(c.getSize());
      ((PagedResultsImpl) ctl).setCookie(c.getCookie());
      ((PagedResultsImpl) ctl).setCritical(c.getCriticality());
    } else if (PasswordPolicyControl.OID.equals(requestControl.getOID())) {
      final PasswordPolicyControl c = (PasswordPolicyControl) requestControl;
      ctl = new PasswordPolicyImpl();
      ctl.setCritical(c.getCriticality());
    }
    return ctl;
  }


  /** {@inheritDoc} */
  @Override
  public org.ldaptive.control.ResponseControl processResponse(
    final Control responseControl)
  {
    org.ldaptive.control.ResponseControl ctl = null;
    if (PagedResultsControl.OID.equals(responseControl.getOid())) {
      final PagedResults c = (PagedResults) responseControl;
      ctl = new PagedResultsControl(c.getSize(), c.getCookie(), c.isCritical());
    } else if (PasswordPolicyControl.OID.equals(responseControl.getOid())) {
      final PasswordPolicy c = (PasswordPolicy) responseControl;
      if (c.hasResponse()) {
        ctl = new PasswordPolicyControl(c.isCritical());

        final PasswordPolicyResponse ppr = c.getResponse();
        ((PasswordPolicyControl) ctl).setTimeBeforeExpiration(
          ppr.getTimeBeforeExpiration());
        ((PasswordPolicyControl) ctl).setGraceAuthNsRemaining(
          ppr.getGraceAuthNsRemaining());

        final PasswordPolicyErrorEnum error = ppr.getPasswordPolicyError();
        if (error != null) {
          ((PasswordPolicyControl) ctl).setError(
            PasswordPolicyControl.Error.valueOf(error.getValue()));
        }
      }
    }
    return ctl;
  }
}
