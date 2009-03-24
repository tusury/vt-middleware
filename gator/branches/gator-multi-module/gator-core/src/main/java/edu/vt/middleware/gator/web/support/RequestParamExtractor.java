/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.web.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper class that pulls parameters out of pretty URLs.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class RequestParamExtractor
{
  protected static final Pattern PROJECT_NAME_PATTERN =
    Pattern.compile("/project/([^/]+)/");

  protected static final Pattern APPENDER_ID_PATTERN =
    Pattern.compile("/appender/(\\d+)/");

  protected static final Pattern CATEGORY_ID_PATTERN =
    Pattern.compile("/category/(\\d+)/");

  protected static final Pattern CLIENT_ID_PATTERN =
    Pattern.compile("/client/(\\d+)/");

  protected static final Pattern PERMISSION_ID_PATTERN =
    Pattern.compile("/perm/(\\d+)/");

  /**
   * Extracts the project name from a request URI of the form
   * /project/NAME/file.html.
   * @param request Request to extract project name from.
   * @return Project name in given request or null if no project name is
   * found in the URI.
   */
  public static String getProjectName(final HttpServletRequest request)
  {
    final Matcher m = PROJECT_NAME_PATTERN.matcher(request.getRequestURI());
    return m.find() ? m.group(1) : null;
  }

  /**
   * Extracts the appender ID from a request URI of the form
   * /project/ID/appender/APPID/file.html.
   * @param request Request to extract appender ID from.
   * @return Appender ID in given request.
   */
  public static int getAppenderId(final HttpServletRequest request)
  {
    return getParam(APPENDER_ID_PATTERN, request.getRequestURI());
  }

  /**
   * Extracts the category ID from a request URI of the form
   * /project/ID/category/CATID/file.html.
   * @param request Request to extract category ID from.
   * @return Category ID in given request.
   */
  public static int getCategoryId(final HttpServletRequest request)
  {
    return getParam(CATEGORY_ID_PATTERN, request.getRequestURI());
  }

  /**
   * Extracts the client ID from a request URI of the form
   * /project/ID/client/CLIENTID/file.html.
   * @param request Request to extract client ID from.
   * @return Client ID in given request.
   */
  public static int getClientId(final HttpServletRequest request)
  {
    return getParam(CLIENT_ID_PATTERN, request.getRequestURI());
  }

  /**
   * Extracts the permission ID from a request URI of the form
   * /project/ID/perm/PERMISSIONID/file.html.
   * @param request Request to extract permission ID from.
   * @return Permission ID in given request.
   */
  public static int getPermissionId(final HttpServletRequest request)
  {
    return getParam(PERMISSION_ID_PATTERN, request.getRequestURI());
  }

  /**
   * Extracts a parameter from a URL string using the given pattern.
   * @param pattern Pattern that contains a capture expression to extract
   * the parameter.
   * @param url URL path string.
   * @return Parameter value or -1 if no matching parameter is found.
   */
  protected static int getParam(final Pattern pattern, final String url)
  {
    final Matcher m = pattern.matcher(url);
    if (m.find()) {
      return Integer.parseInt(m.group(1));
    } else {
      return -1;
    }
  }
}
