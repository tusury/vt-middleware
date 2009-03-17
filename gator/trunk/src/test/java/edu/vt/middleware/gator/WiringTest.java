/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * Tests Spring application context wiring.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class WiringTest
{
  /**
   * Setup routine called before each test.
   */
  @Before
  public void setUp()
  {
    // Set system properties for placeholders used in the contexts
//    System.setProperty("jdbc.driverClass", "org.hsqldb.jdbcDriver");
//    System.setProperty("jdbc.url", "jdbc:hsqldb:mem:gator");
//    System.setProperty("jdbc.user", "sa");
//    System.setProperty("jdbc.pass", "");
//    System.setProperty("jdbc.pool.initSize", "1");
//    System.setProperty("jdbc.pool.maxIdle", "1");
//    System.setProperty("jdbc.pool.maxActive", "1");
//    System.setProperty("db.dialect", "org.hibernate.dialect.HSQLDialect");
//    System.setProperty("db.batchSize", "0");
//    System.setProperty("log4j.client.root.dir", "target/logs");
//    System.setProperty("log4j.server.bindAddress", "127.0.0.1");
//    System.setProperty("log4j.server.port", "8000");
  }

  /** Tests Spring context wiring */
  @Test
  public void testWiring()
  {
    final FileSystemXmlApplicationContext context =
      new FileSystemXmlApplicationContext(new String[] {
        "src/main/webapp/WEB-INF/applicationContext.xml",
        "src/main/webapp/WEB-INF/applicationContext-authz.xml",
        "src/main/webapp/WEB-INF/gator-servlet.xml",
        "securityContext.xml",
      });
    Assert.assertTrue(context.getBeanDefinitionCount() > 0);
  }
}
