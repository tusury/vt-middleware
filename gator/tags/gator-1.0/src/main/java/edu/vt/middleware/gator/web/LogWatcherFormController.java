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
package edu.vt.middleware.gator.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.log4j.LoggingEventCollector;
import edu.vt.middleware.gator.log4j.LoggingEventHandler;
import edu.vt.middleware.gator.log4j.SocketServer;

/**
 * Controller for watching collected logging events in real time.
 * Provides an form interface for configuring the categories to display,
 * then displays the logging events from those categories in the HTTP response
 * as raw plain/text data.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
public class LogWatcherFormController extends BaseFormController
{
  /** Socket server */
  private SocketServer socketServer;


  /**
   * @param socketServer the socketServer to set
   */
  public void setSocketServer(final SocketServer socketServer)
  {
    this.socketServer = socketServer;
  }

  /** {@inheritDoc} */
  @Override
  public void afterPropertiesSet() throws Exception
  {
    super.afterPropertiesSet();
    Assert.notNull(socketServer, "SocketServer is required.");
  }

  /** {@inheritDoc} */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
    throws Exception
  {
    final ProjectConfig project = configManager.findProject(
        RequestParamExtractor.getProjectName(request));
    if (project == null) {
      throw new IllegalArgumentException("No project specified.");
    }
    final WatchConfig wc = new WatchConfig();
    wc.setProjectId(project.getId());
    return wc;
  }

  /** {@inheritDoc} */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request)
    throws Exception
  {
    final Map<String, Object> data = new HashMap<String, Object>();
    data.put(
      "project",
      configManager.findProject(RequestParamExtractor.getProjectName(request)));
    return data;
  }

  /** {@inheritDoc} */
  @Override
  protected ModelAndView onSubmit(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final Object command,
    final BindException errors)
    throws Exception
  {
    final WatchConfig wc = (WatchConfig) command;
    final LoggingEventCollector collector = new LoggingEventCollector(1000);
    try {
      addLoggingEventCollector(collector);
      writeResponse(response, wc, collector);
    } catch (IOException e) {
      logger.debug("Caught IO exception while writing logging events.");
    } finally {
      removeLoggingEventCollector(collector);
    }
    // Send null to signal Spring MVC that we've dealt with response
    // data inside the controller and don't need to render a view
    return null;
  }

  /**
   * Writes logging events received by the collector to a
   * {@link WriterAppender} that wraps the HTTP response stream.
   *
   * @param response HTTP response.
   * @param wc Configuration object for watching logs.
   * @param collector Receptor of logging events to write.
   *
   * @throws IOException On the first error flushing the HTTP response,
   * which in many cases will occur on close. This exception should be
   * handled as an end-of-stream case as well as an error condition.
   */
  private void writeResponse(
    final HttpServletResponse response,
    final WatchConfig wc,
    final LoggingEventCollector collector)
    throws IOException
  {
    final ProjectConfig project = configManager.find(
      ProjectConfig.class,
      wc.getProjectId());
    final PrintWriter writer = response.getWriter();
    final LoggerRepository repo = new Hierarchy(new RootLogger(Level.ALL));
    final WriterAppender appender = new WriterAppender();
    appender.setWriter(writer);
    final PatternLayout layout = new PatternLayout();
    layout.setConversionPattern(wc.getLayoutConversionPattern());
    appender.setLayout(layout);
    for (int categoryId : wc.getCategoryIds()) {
      final CategoryConfig category = project.getCategory(categoryId);
      final Logger logger = repo.getLogger(category.getName());
      logger.setLevel(category.getLog4jLevel());
      logger.addAppender(appender);
    }
    response.setContentType("text/plain");
    writer.println("Logging output will appear below as it is received.");
    writer.println("===================================================");
    response.flushBuffer();
    int waitCycles = 0;
    while (!writer.checkError()) {
      try {
        // Time out after a short wait to allow periodic stream close check
        final LoggingEvent event =
          collector.getEventQueue().poll(10, TimeUnit.SECONDS);
        if (event != null) {
          if (waitCycles > 0) {
            writer.println();
            waitCycles = 0;
          }
	        final Logger logger = repo.getLogger(event.getLoggerName());
	        if(event.getLevel().isGreaterOrEqual(logger.getEffectiveLevel())) {
	          logger.callAppenders(event);
	        }
        } else {
          waitCycles++;
          writer.print('.');
        }
      } catch (InterruptedException e) {
        logger.debug("Interrupted waiting for logging event.");
      } finally {
        writer.flush();
      }
    }
  }

  /**
   * Adds the given logging event collector to all logging event handlers
   * attached to the socket server.
   * @param collector Collector to add.
   */
  private void addLoggingEventCollector(final LoggingEventCollector collector)
  {
    for (LoggingEventHandler handler : socketServer.getLoggingEventHandlers())
    {
      handler.getLoggingEventListeners().add(collector);
    }
  }

  /**
   * Removes the given logging event collector from all logging event handlers
   * attached to the socket server.
   * @param collector Collector to add.
   */
  private void removeLoggingEventCollector(
    final LoggingEventCollector collector)
  {
    for (LoggingEventHandler handler : socketServer.getLoggingEventHandlers())
    {
      handler.getLoggingEventListeners().remove(collector);
    }
  }  

  /**
   * Form backing object for log watching configuration.
   *
   * @author Middleware
   * @version $Revision$
   *
   */
  public static class WatchConfig
  {
    /** Default appender layout conversion pattern */
    private static final String DEFAULT_CONVERSION_PATTERN =
      "%X{host} %d %-5p [%c] %m%n";
   
    /** ID of project whose logging events will be watched */
    private int projectId;
   
    /** Appender layout string */
    private String layoutConversionPattern = DEFAULT_CONVERSION_PATTERN;
   
    /** Array of enabled category IDs */
    private int[] categoryIds;

    /**
     * @return the projectId
     */
    public int getProjectId()
    {
      return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(int projectId)
    {
      this.projectId = projectId;
    }

    /**
     * @return the layoutConversionPattern
     */
    public String getLayoutConversionPattern()
    {
      return layoutConversionPattern;
    }

    /**
     * @param layoutConversionPattern the layoutConversionPattern to set
     */
    public void setLayoutConversionPattern(String layoutConversionPattern)
    {
      this.layoutConversionPattern = layoutConversionPattern;
    }

    /**
     * @return the categoryIds
     */
    public int[] getCategoryIds()
    {
      return categoryIds;
    }

    /**
     * @param categoryIds the categoryIds to set
     */
    public void setCategoryIds(int[] categoryIds)
    {
      this.categoryIds = categoryIds;
    }
  }
}
