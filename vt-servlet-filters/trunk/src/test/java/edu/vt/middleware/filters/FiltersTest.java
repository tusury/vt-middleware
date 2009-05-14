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
package edu.vt.middleware.filters;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link edu.vt.middleware.filters}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class FiltersTest
{

  /** To test servlets with. */
  private ServletRunner servletRunner;


  /**
   * @param  webXml  web.xml for queries
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "webXml" })
  @BeforeClass(groups = {"filtertest"})
  public void setup(final String webXml)
    throws Exception
  {
    this.servletRunner = new ServletRunner(webXml);
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"filtertest"})
  public void sessionAttributeFilter()
    throws Exception
  {
    final ServletUnitClient sc = this.servletRunner.newClient();
    sc.setExceptionsThrownOnErrorStatus(false);
    WebRequest request = new GetMethodWebRequest(
      "http://filters.middleware.vt.edu/AttributeFilterTestServlet");

    WebResponse response = sc.getResponse(request);
    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals(403, response.getResponseCode());

    request = new GetMethodWebRequest(
      "http://filters.middleware.vt.edu/SessionTestServlet");
    request.setParameter("user", "testuser");
    request.setParameter(
      "redirect",
       "http://filters.middleware.vt.edu/AttributeFilterTestServlet");

    response = sc.getResponse(request);
    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals(200, response.getResponseCode());
    final String responseText = response.getText();
    AssertJUnit.assertTrue(responseText.startsWith("FilterTestServlet"));
    AssertJUnit.assertTrue(responseText.indexOf("testuser") > 0);
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"filtertest"})
  public void requestMethodFilter()
    throws Exception
  {
    final ServletUnitClient sc = this.servletRunner.newClient();
    sc.setExceptionsThrownOnErrorStatus(false);

    WebRequest request = new PostMethodWebRequest(
      "http://filters.middleware.vt.edu/RequestMethodFilterTestServlet");
    WebResponse response = sc.getResponse(request);
    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals(403, response.getResponseCode());

    request = new GetMethodWebRequest(
      "http://filters.middleware.vt.edu/RequestMethodFilterTestServlet");
    response = sc.getResponse(request);
    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals(200, response.getResponseCode());
    final String responseText = response.getText();
    AssertJUnit.assertTrue(responseText.startsWith("FilterTestServlet"));
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"filtertest"})
  public void clientCertFilter()
    throws Exception
  {
    final ServletUnitClient sc = this.servletRunner.newClient();
    sc.setExceptionsThrownOnErrorStatus(false);

    WebRequest request = new GetMethodWebRequest(
      "http://filters.middleware.vt.edu/FailClientCertFilterTestServlet");
    WebResponse response = sc.getResponse(request);
    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals(403, response.getResponseCode());

    request = new GetMethodWebRequest(
      "http://filters.middleware.vt.edu/ClientCertFilterTestServlet");
    response = sc.getResponse(request);
    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals(200, response.getResponseCode());
    final String responseText = response.getText();
    AssertJUnit.assertTrue(responseText.startsWith("FilterTestServlet"));
  }
}
