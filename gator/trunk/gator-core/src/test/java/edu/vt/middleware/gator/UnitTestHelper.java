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

import java.util.Calendar;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ClientConfig;
import edu.vt.middleware.gator.LayoutParamConfig;
import edu.vt.middleware.gator.ProjectConfig;

/**
 * Test helper utility class.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class UnitTestHelper
{
  /** Creates a new instance */
  protected UnitTestHelper() {}
  
  
  public static ProjectConfig createProject(
      final String projectName,
      final String appender1Name,
      final String appender2Name,
      final String client1Name,
      final String client2Name,
      final String ... categories)
  {
    final ProjectConfig project = new ProjectConfig();
    project.setName(projectName);
    project.setModifiedDate(Calendar.getInstance());
    project.setClientLogDir("target");
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
    appender.setAppenderClassName(
        "org.apache.log4j.RollingFileAppender");
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
