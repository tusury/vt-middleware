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
import edu.vt.middleware.gator.log4j.SocketServer;

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


  /**
   * Creates a fully-functional project configuration for use in testing.
   * @return Functional test project.
   */
  public static ProjectConfig createTestProject()
  {
    final AppenderConfig fileAppender = new AppenderConfig();
    fileAppender.setName("FILE");
    fileAppender.setAppenderClassName("org.apache.log4j.FileAppender");
    fileAppender.setLayoutClassName("org.apache.log4j.PatternLayout");
    final AppenderParamConfig fileParam = new AppenderParamConfig();
    fileParam.setName("file");
    fileParam.setValue("file.log");
    fileAppender.addAppenderParam(fileParam);
    final LayoutParamConfig fileLayoutParam = new LayoutParamConfig();
    fileLayoutParam.setName("conversionPattern");
    fileLayoutParam.setValue("%d %-5p [%c] %m%n");
    fileAppender.addLayoutParam(fileLayoutParam);
    
    final AppenderConfig rollingFileAppender = new AppenderConfig();
    rollingFileAppender.setName("ROLLING_FILE");
    rollingFileAppender.setAppenderClassName(
        "org.apache.log4j.RollingFileAppender");
    rollingFileAppender.setLayoutClassName("org.apache.log4j.PatternLayout");
    final AppenderParamConfig rollingFileParam1 = new AppenderParamConfig();
    rollingFileParam1.setName("file");
    rollingFileParam1.setValue("rolling-file.log");
    final AppenderParamConfig rollingFileParam2 = new AppenderParamConfig();
    rollingFileParam2.setName("maxBackupIndex");
    rollingFileParam2.setValue("1");
    rollingFileAppender.addAppenderParam(rollingFileParam1);
    rollingFileAppender.addAppenderParam(rollingFileParam2);
    final LayoutParamConfig rollingFileLayoutParam = new LayoutParamConfig();
    rollingFileLayoutParam.setName("conversionPattern");
    rollingFileLayoutParam.setValue("%d %-5p [%c] %m%n");
    rollingFileAppender.addLayoutParam(rollingFileLayoutParam);
    
    final CategoryConfig category = new CategoryConfig();
    category.setName("edu.vt.middleware.gator");
    category.setLevel("DEBUG");
    category.getAppenders().add(fileAppender);
    category.getAppenders().add(rollingFileAppender);
    
    final ClientConfig client = new ClientConfig();
    client.setName(SocketServer.DEFAULT_BIND_ADDRESS);

    final ProjectConfig project = new ProjectConfig();
    project.setName("Test Project");
    project.setModifiedDate(Calendar.getInstance());
    project.setClientLogDir("target");
    project.addAppender(fileAppender);
    project.addAppender(rollingFileAppender);
    project.addCategory(category);
    project.addClient(client);
    
    project.addPermission(
      new PermissionConfig("admin", PermissionConfig.parsePermissions("rwd")));
    project.addPermission(
      new PermissionConfig("user", PermissionConfig.parsePermissions("r")));

    return project;
  }
}
