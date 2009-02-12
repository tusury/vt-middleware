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
package edu.vt.middleware.gator.log4j;

import java.util.Enumeration;

import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.JpaConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.UnitTestHelper;
import edu.vt.middleware.gator.log4j.Configurator;
import edu.vt.middleware.gator.log4j.JdbcConfigurator;

import org.apache.log4j.Appender;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link JdbcConfigurator} test.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
public class JdbcConfiguratorTest
{
  /** Subject of test */
  private Configurator configurator;


  /**
   * Test setup routine called before each test method.
   * @throws Exception On errors.
   */
  @Before
  public void setUp() throws Exception
  {
    configurator = new JdbcConfigurator();
    configurator.setClientRootLogDirectory("target/logs");
    configurator.setConfigManager(new JpaConfigManager());
  }


  /**
   * Test method for
   * {@link JdbcConfigurator#configure(ProjectConfig, LoggerRepository)}.
   * @throws Exception On errors.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testConfigureProjectConfigLoggerRepository() throws Exception
  {
    final ProjectConfig project = UnitTestHelper.createTestProject(); 
    final LoggerRepository repo =
      new Hierarchy(new RootLogger(Level.INFO));
    configurator.configure(project, repo);
    int categoryCount = 0;
    for (CategoryConfig category : project.getCategories()) {
      Logger l = repo.getLogger(category.getName());
      int appenderCount = 0;
      if (l != null && l.getName() == category.getName()) {
        categoryCount++;
        final Enumeration<Appender> eAppenders = l.getAllAppenders();
        while (eAppenders.hasMoreElements()) {
          appenderCount++;
          eAppenders.nextElement();
        }
      }
      Assert.assertEquals(category.getAppenders().size(), appenderCount);
    }
    Assert.assertEquals(project.getCategories().size(), categoryCount);
  }
}
