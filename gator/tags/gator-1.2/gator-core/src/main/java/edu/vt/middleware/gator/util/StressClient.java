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
package edu.vt.middleware.gator.util;

import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;

import edu.vt.middleware.gator.log4j.SocketServer;

/**
 * Simple test program that connects to a log4j socket server and sends a large
 * number of logging events over the socket.  May be used for stress testing.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class StressClient
{
  /** Default milliseconds between successive logging events */
  public static final int DEFAULT_IDLE_TIME = 100;
 
  /** Default category of logging events sent by this client */
  public static final String DEFAULT_CATEGORY = "edu.vt.middleware.gator";
 
  /** Logger levels */
  private static final Level[] LEVELS = new Level[] {
    Level.FATAL,
    Level.ERROR,
    Level.WARN,
    Level.INFO,
    Level.DEBUG,
    Level.ERROR,
  };


  /**
   * Stress client entry point.
   *
   * @param args Expects the following arguments:
   * <ol>
   * <li>0 - host name</li>
   * <li>1 - port number</li>
   * <li>2 (optional) - idle time between in ms between successive logging
   * events</li>
   * <li>3 (optional) - logger category of test events</li>
   * </ol>
   */
  public static void main(String[] args)
  {
    String host = SocketServer.DEFAULT_BIND_ADDRESS;
    int port = SocketServer.DEFAULT_PORT;
    int idle = DEFAULT_IDLE_TIME;
    String category = DEFAULT_CATEGORY;
    try {
      host = args[0];
      port = Integer.parseInt(args[1]);
      if (args.length > 2) {
        idle = Integer.parseInt(args[2]);
      }
      if (args.length > 3) {
        category = args[3];
      }
    } catch (Exception ex) {
      System.out.println("USAGE: StressClient host port [idle_ms] [category]");
      return;
    }
    final SocketAppender appender = new SocketAppender(host, port);
    final Random rnd = new Random(System.currentTimeMillis());
    try {
      System.out.println("Starting stress client.");
      System.out.println(
        String.format(
          "Sending logging event in category '%s' every %s ms.",
          category,
          idle));
      System.out.println("Log level varies randomly.");
      System.out.println("Terminate process to stop.");
      System.out.println("Running");
      long n = 1;
      while (true) {
        System.out.print('.');
        appender.append(
          new LoggingEvent(
            category,
            Logger.getLogger(category),
            System.currentTimeMillis(),
            LEVELS[rnd.nextInt(LEVELS.length)],
            "Stress client test logging event #" + n++,
            null));
        try {
          Thread.sleep(idle);
        } catch (InterruptedException e) {
          // Treat interruption as signal to quit
          break;
        }
      }
    } finally {
      appender.close();
    }
  }

}
