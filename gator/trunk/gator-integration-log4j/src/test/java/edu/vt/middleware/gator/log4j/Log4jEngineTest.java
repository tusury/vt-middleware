/*
  $Id$

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.gator.log4j;

import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.LayoutParamConfig;
import edu.vt.middleware.gator.ProjectConfig;

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
 * Unit test for {@link Log4jEngine} test.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class Log4jEngineTest
{
  /** Directory containing log files. */
  private static final String LOG_DIR = "target/logs";

  /** Subject of test. */
  private Log4jEngine engine;


  /**
   * Test setup routine called before each test method.
   *
   * @throws  Exception  On errors.
   */
  @Before
  public void setUp() throws Exception
  {
    new File(LOG_DIR).mkdirs();
    engine = new Log4jEngine(LOG_DIR);
  }


  /**
   * Test method for {@link ProjectConfigurator#configure(ProjectConfig,
   * LoggerRepository)}.
   *
   * @throws  Exception  On errors.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testConfigure() throws Exception
  {
    final ProjectConfig project = createProject(
      "p",
      "a1",
      "a2",
      "c1",
      "c2",
      "cat1",
      "cat2");
    final LoggerRepository repo = new Hierarchy(new RootLogger(Level.INFO));
    engine.configureInternal(project, repo);

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
  public static ProjectConfig createProject(
      final String projectName,
      final String appender1Name,
      final String appender2Name,
      final String client1Name,
      final String client2Name,
      final String... categories)
    {
      final ProjectConfig project = new ProjectConfig();
      project.setName(projectName);
      project.setModifiedDate(Calendar.getInstance());
      project.setClientLogDir("target");
      project.setLoggingEngine("edu.vt.middleware.gator.mock.MockEngine");
      project.addAppender(createAppender(appender1Name));
      project.addAppender(createAppender(appender2Name));
      project.addClient(createClient(client1Name));
      project.addClient(createClient(client2Name));
      for (String category : categories) {
        final CategoryConfig c = createCategory(category);
        for (AppenderConfig appender : project.getAppenders()) {
          c.getAppenders().add(appender);
        }
        project.addCategory(c);
      }
      return project;
    }

    public static AppenderConfig createAppender(final String name)
    {
      final AppenderConfig appender = new AppenderConfig();
      appender.setName(name);
      appender.setAppenderClassName("org.apache.log4j.RollingFileAppender");
      appender.setLayoutClassName("org.apache.log4j.PatternLayout");

      final AppenderParamConfig rollingFileParam1 = new AppenderParamConfig();
      rollingFileParam1.setName("file");
      rollingFileParam1.setValue(name + "-file.log");

      final AppenderParamConfig rollingFileParam2 = new AppenderParamConfig();
      rollingFileParam2.setName("maxBackupIndex");
      rollingFileParam2.setValue("1");
      appender.addAppenderParam(rollingFileParam1);
      appender.addAppenderParam(rollingFileParam2);

      final LayoutParamConfig rollingFileLayoutParam = new LayoutParamConfig();
      rollingFileLayoutParam.setName("conversionPattern");
      rollingFileLayoutParam.setValue("%d %-5p [%c] %m%n");
      appender.addLayoutParam(rollingFileLayoutParam);
      return appender;
    }

    public static CategoryConfig createCategory(final String name)
    {
      final CategoryConfig category = new CategoryConfig();
      category.setName(name);
      category.setLevel("DEBUG");
      return category;
    }

    public static ClientConfig createClient(final String name)
    {
      final ClientConfig client = new ClientConfig();
      client.setName(name);
      return client;
    }
}
