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
package org.ldaptive.auth.ext;

import java.util.Calendar;
import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.AuthenticationResponseHandler;
import org.ldaptive.control.PasswordPolicyControl;
import org.ldaptive.control.ResponseControl;

/**
 * Attempts to parse the authentication response message and set the account
 * state using data associated with a password policy control.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PasswordPolicyAuthenticationResponseHandler
  implements AuthenticationResponseHandler
{


  /** {@inheritDoc} */
  @Override
  public void process(final AuthenticationResponse response)
  {
    if (response.getControls() != null) {
      for (ResponseControl control : response.getControls()) {
        if (control instanceof PasswordPolicyControl) {
          final PasswordPolicyControl ppc = (PasswordPolicyControl) control;
          Calendar exp = null;
          if (ppc.getTimeBeforeExpiration() > 0) {
            exp = Calendar.getInstance();
            exp.add(Calendar.SECOND, ppc.getTimeBeforeExpiration());
          }
          if (exp != null || ppc.getGraceAuthNsRemaining() > 0) {
            response.setAccountState(
              new PasswordPolicyAccountState(
                exp,
                ppc.getGraceAuthNsRemaining()));
          }
          if (response.getAccountState() == null && ppc.getError() != null) {
            response.setAccountState(
              new PasswordPolicyAccountState(ppc.getError()));
          }
          break;
        }
      }
    }
  }
}
