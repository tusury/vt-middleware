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
package org.ldaptive.auth.ext;

import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.AuthenticationResponseHandler;

/**
 * Attempts to parse the authentication response message and set the account
 * state using data associated with active directory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ActiveDirectoryAuthenticationResponseHandler
  implements AuthenticationResponseHandler
{


  /** {@inheritDoc} */
  @Override
  public void process(final AuthenticationResponse response)
  {
    if (response.getMessage() != null) {
      final ActiveDirectoryAccountState.Error adError =
        ActiveDirectoryAccountState.Error.parse(response.getMessage());
      if (adError != null) {
        response.setAccountState(new ActiveDirectoryAccountState(adError));
      }
    }
  }
}
