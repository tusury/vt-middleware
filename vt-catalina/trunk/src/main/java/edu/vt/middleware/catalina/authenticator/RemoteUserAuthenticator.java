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
package edu.vt.middleware.catalina.authenticator;

import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;

/**
 * An <b>Authenticator</b> and <b>Valve</b> implementation of authentication
 * that utilizes the REMOTE_USER header to invoke a realm.
 * Authentication is implied by the existance of the REMOTE_USER.
 * Any realm invoked by this authenticator must be prepared to accept a null password.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class RemoteUserAuthenticator extends AuthenticatorBase {

    public static final String REMOTE_USER_METHOD = "REMOTE-USER";

    // ------------------------------------------------------------- Properties

    /**
     * Descriptive information about this implementation.
     */
    protected static final String info =
        "edu.vt.middleware.catalina.authenticator.RemoteUserAuthenticator/1.0";


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {
        return (info);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Authenticate the user by checking for the existence of a principal.
     * If found, the principal name is passed to realm authenticate with a null password.
     *
     * @param request Request we are processing
     * @param response Response we are creating
     * @param config    Login configuration describing how authentication
     *              should be performed
     *
     * @exception IOException if an input/output error occurs
     */
    public boolean authenticate(Request request,
                                Response response,
                                LoginConfig config)
        throws IOException {

        // Confirm the user has been authenticated
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            if (containerLog.isDebugEnabled())
                containerLog.debug("  No principal included with this request");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                               "Principal not found");
            return (false);
        }

        if (containerLog.isDebugEnabled())
            containerLog.debug("Found principal '" + principal.getName() + "'");
        // Re-authenticate the principal
        Principal newPrincipal = context.getRealm().authenticate(
            principal.getName(), (String) null);
        if (newPrincipal == null) {
            if (containerLog.isDebugEnabled())
                containerLog.debug("  Realm.authenticate() returned false");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                               sm.getString("authenticator.unauthorized"));
            return (false);
        }

        // Cache the new principal (if requested) and record this authentication
        register(request, response, newPrincipal, REMOTE_USER_METHOD,
                 null, null);
        return (true);
    }

    // ------------------------------------------------------ Lifecycle Methods

    /**
     * Initialize the database we will be using for client verification
     * and certificate validation (if any).
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {
        super.start();
    }


    /**
     * Finalize the database we used for client verification and
     * certificate validation (if any).
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void stop() throws LifecycleException {
        super.stop();
    }
}
