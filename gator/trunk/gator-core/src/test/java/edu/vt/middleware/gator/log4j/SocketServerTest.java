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


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.ConfigManager;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.UnitTestHelper;
import edu.vt.middleware.gator.log4j.Configurator;
import edu.vt.middleware.gator.log4j.SocketServer;
import edu.vt.middleware.gator.util.FileHelper;

/**
 * Unit test for the {@link SocketServer} class.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/test-applicationContext.xml"})
@TransactionConfiguration(transactionManager="txManager", defaultRollback=false)
@Transactional
public class SocketServerTest
{
  /** Test logger */
  private static final Log LOGGER = LogFactory.getLog("edu.vt.middleware.test");

  /** Client root directory */
  private static final String CLIENT_ROOT_DIR = "target/logs";

  /** Socket connection timeout period */
  private static final int SOCKET_CONNECT_TIMEOUT = 5;
 
  /** Text of test message */ 
  private static final String TEST_MESSAGE = "Test message from JUnit.";
 
  /** Test logging event category */
  private static final String TEST_CATEGORY = "edu.vt.middleware.foo";
 
  /** Test project configuration */
  private ProjectConfig testProject;

  /** Transaction manager */
  @Autowired
  private PlatformTransactionManager txManager;
  
  /** Handles log4j repository configuration */
  @Autowired
  private Configurator configurator;
 
  /** Handles persisting projects */
  @Autowired
  private ConfigManager configManager;
 
  /** Subject of test */
  private SocketServer server;


  /**
   * Test setup routine called before each test method.
   * @throws Exception On errors.
   */
  @BeforeTransaction
  public void setUp() throws Exception
  {
    testProject = UnitTestHelper.createProject(
        "p", "a1", "a2",
        SocketServer.DEFAULT_BIND_ADDRESS, "127.0.0.2", TEST_CATEGORY);
    new TransactionTemplate(txManager).execute(
        new TransactionCallbackWithoutResult() {
          protected void doInTransactionWithoutResult(
            final TransactionStatus status) {
            configManager.save(testProject);
          }
        });
    server = new SocketServer();
    server.setConfigurator(configurator);
    server.setClientRemovalPolicy(new NoopClientRemovalPolicy());
    server.setStartOnInit(true);
  }

 
  /**
   * Tests connecting to the socket server and sending a logging event
   * that should be written to configured appenders.
   * @throws Exception On errors.
   */
  @Test
  public void testConnectAndLog() throws Exception
  {
    server.init();
    final Socket sock = new Socket();
    try {
      final SocketAddress addr = new InetSocketAddress(
          InetAddress.getByName(SocketServer.DEFAULT_BIND_ADDRESS),
          SocketServer.DEFAULT_PORT);
      sock.connect(addr, SOCKET_CONNECT_TIMEOUT);
      // Allow the socket server time to build the hierarchy
      // before sending a test logging event
      Thread.sleep(5000);
      LOGGER.debug("Sending test logging event.");
      final LoggingEvent event = new LoggingEvent(
          TEST_CATEGORY,
          Logger.getLogger(TEST_CATEGORY),
          Level.DEBUG,
          TEST_MESSAGE,
          null);
      final ObjectOutputStream oos = new ObjectOutputStream(
          sock.getOutputStream());
      oos.writeObject(event);
      oos.flush();
    } finally {
      if (sock.isConnected()) {
        sock.close();
      }
    }
    // Pause to allow time for logging events to be written
    Thread.sleep(1000);
    for (AppenderConfig appender : testProject.getAppenders()) {
      final String logFilePath = FileHelper.pathCat(
        CLIENT_ROOT_DIR,
        testProject.getName(),
        appender.getAppenderParam("file").getValue());
      final String contents = readTextFile(logFilePath);
      Assert.assertTrue(contents.contains(TEST_MESSAGE));
    }
    Assert.assertEquals(1, server.eventHandlerMap.keySet().size());
    server.stop();
    Assert.assertEquals(0, server.eventHandlerMap.keySet().size());
  }


  /**
   * Test cleanup routine called after each test method.
   * @throws Exception On errors.
   */
  @AfterTransaction
  public void tearDown() throws Exception
  {
    new TransactionTemplate(txManager).execute(
        new TransactionCallbackWithoutResult() {
          protected void doInTransactionWithoutResult(
            final TransactionStatus status) {

            final ProjectConfig projectFromDb = configManager.find(
                ProjectConfig.class,
                testProject.getId());
            if (projectFromDb != null) {
              configManager.delete(projectFromDb);
            }
          }
        });
  }
 
 
  /**
   * Reads the contents of the text file at the given path into a string and
   * returns it.
   * @param filePath Path to file to read.
   * @return Contents of file or null if file does not exist or is empty.
   * @throws IOException On read errors.
   */
  private String readTextFile(final String filePath) throws IOException
  {
    final BufferedReader reader = new BufferedReader(new FileReader(filePath));
    final StringWriter writer = new StringWriter();
    String line = null;
    while ((line = reader.readLine()) != null) {
      writer.write(line);
    }
    return writer.toString();
  }
}
