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
package edu.vt.middleware.gator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;

import edu.vt.middleware.gator.log4j.SocketServer;

/**
 * Command line test client for sending logging events to a server.
 *
 * @author Marvin S. Addison
 *
 */
public class TestClient
{
  /**
   * Test client entry point.
   *
   * @param args Expects the following arguments:
   * <ol>
   * <li>0 - host name</li>
   * <li>1 - port number</li>
   * </ol>
   */
  public static void main(final String[] args)
  {
    String host = SocketServer.DEFAULT_BIND_ADDRESS;
    int port = SocketServer.DEFAULT_PORT;
    try {
      host = args[0];
      port = Integer.parseInt(args[1]);
    } catch (Exception ex) {
      System.out.println("USAGE: TestClient host port");
      return;
    }
    final SocketAppender appender = new SocketAppender(host, port);
    try {
      boolean running = true;
      System.out.println(
      "The test client enables writing arbitrary log messages.");
      System.out.println("Format is category|level|message.");
      while (running) {
        System.out.println("Type a log message, -1 to quit:");
        final String input = readLine();
        if (input.equals("-1")) {
          System.out.println("Quitting on user input.");
          running = false;
        } else {
          final String[] items = input.trim().split("\\|");
          LoggingEvent event = null;
          try {
            event = new LoggingEvent(
                items[0],
                Logger.getLogger(items[0]),
                System.currentTimeMillis(),
                Level.toLevel(items[1]),
                items[2],
                null);
          } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("WARNING: input not in required format.");
          }
          if (event != null) {
            appender.append(event);
          }
        }
      }
    } finally {
      appender.close();
    }
  }
 
  /**
   * Reads a line of input from stdin.
   * @return Line as a string.
   */
  private static String readLine()
  {
    final BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in));
    try {
      final String line = reader.readLine();
      System.out.println("Read " + line);
      return line;
    } catch (IOException e) {
      e.printStackTrace();
      return "-1";
    }
  }
}
