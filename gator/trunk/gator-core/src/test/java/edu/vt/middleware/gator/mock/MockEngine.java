/*
  $Id: $

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: $
  Updated: $Date: $
*/
package edu.vt.middleware.gator.mock;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.server.ConfigurationException;
import edu.vt.middleware.gator.server.EngineException;
import edu.vt.middleware.gator.server.LoggingEngine;
import edu.vt.middleware.gator.server.status.ClientStatus;

/**
 * Mock logging engine for testing.
 *
 * @author  Marvin S. Addison
 * @version $Revision: $
 *
 */
public class MockEngine implements LoggingEngine
{
  /** Output file. */
  private final File outFile;

  /** Output stream to write log events to. */
  private PrintStream out;


  /**
   * Creates a new mock logging engine that writes log events to the given
   * file.
   *
   * @param  outFilePath  Path to output file for log events.
   */
  public MockEngine(final String outFilePath)
  {
    outFile = new File(outFilePath);
  }


  /** {@inheritDoc} */
  public void start() throws EngineException
  {
    try {
      outFile.getParentFile().mkdirs();
	    outFile.createNewFile();
	    out = new PrintStream(new BufferedOutputStream(
	        new FileOutputStream(outFile)));
    } catch (IOException e) {
      throw new EngineException("Error creating output file.", e);
    }
  }


  /** {@inheritDoc} */
  public boolean supports(final Object event)
  {
    return event instanceof MockEvent;
  }


  /** {@inheritDoc} */
  public void register(final InetAddress client, final ProjectConfig project)
      throws EngineException {}


  /** {@inheritDoc} */
  public void configure(final ProjectConfig project)
      throws ConfigurationException {}


  /** {@inheritDoc} */
  public void configure(final ClientStatus clientStatus) {}


  /** {@inheritDoc} */
  public void handleEvent(final InetAddress sender, final Object event)
      throws EngineException
  {
    out.print(event);
    out.flush();
  }


  /** {@inheritDoc} */
  public void shutdown()
  {
    out.close();
  }


  /** {@inheritDoc} */
  public String toString(final Object event)
  {
    return event.toString();
  }

}
