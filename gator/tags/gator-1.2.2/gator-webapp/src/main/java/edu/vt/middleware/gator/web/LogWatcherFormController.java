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
package edu.vt.middleware.gator.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.log4j.LoggingEventHandler;
import edu.vt.middleware.gator.log4j.LoggingEventListener;
import edu.vt.middleware.gator.log4j.SocketServer;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller for watching collected logging events in real time. Provides an
 * form interface for configuring the categories to display, then displays the
 * logging events from those categories in the HTTP response as raw plain/text
 * data.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes({ "watchConfig", "project" })
public class LogWatcherFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "watchForm";

  /** Socket server. */
  @Autowired @NotNull
  private SocketServer socketServer;


  @RequestMapping(
    value = "/project/{projectName}/watch.html",
    method = RequestMethod.GET
  )
  public String getWatchConfig(
    @PathVariable("projectName") final String projectName,
    final Model model)
  {
    final ProjectConfig project = getProject(projectName);
    model.addAttribute("project", project);
    model.addAttribute("watchConfig", new WatchConfig(project.getId()));
    return VIEW_NAME;
  }


  @RequestMapping(
    value = "/project/{projectName}/watch.html",
    method = RequestMethod.POST
  )
  @Transactional(propagation = Propagation.REQUIRED)
  public String watch(
    @Valid
    @ModelAttribute("watchConfig")
    final WatchConfig watchConfig,
    final BindingResult result,
    final HttpServletResponse response)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }

    final ProjectConfig project = configManager.find(
      ProjectConfig.class,
      watchConfig.getProjectId());
    final Hierarchy hierarchy = new Hierarchy(new RootLogger(Level.ALL));
    final PatternLayout layout = new PatternLayout();
    layout.setConversionPattern(watchConfig.getLayoutConversionPattern());
    for (int categoryId : watchConfig.getCategoryIds()) {
      final CategoryConfig category = project.getCategory(categoryId);
      final Logger logger = hierarchy.getLogger(category.getName());
      logger.setLevel(category.getLog4jLevel());
    }

    final LoggingEventWriter logEventWriter = new LoggingEventWriter(
      hierarchy,
      layout,
      response);
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting to watch logging events for connected clients.");
      }
      addLoggingEventWriter(logEventWriter, project);
      logEventWriter.waitToFinish();
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Caught exception while writing logging events: " + e);
      }
    } finally {
      if (logger.isDebugEnabled()) {
        logger.debug("Finished watching logs.");
      }
      if (logEventWriter != null) {
        removeLoggingEventWriter(logEventWriter, project);
      }
    }
    // Send null to signal Spring MVC that we've dealt with response
    // data inside the controller and don't need to render a view
    return null;
  }


  /**
   * Adds the given logging event writer to all logging event handlers for
   * clients that belong to the given project.
   *
   * @param  writer  Writer to add.
   * @param  project  Project for which clients' handlers should have writer
   * added.
   */
  private void addLoggingEventWriter(
    final LoggingEventWriter writer,
    final ProjectConfig project)
  {
    for (LoggingEventHandler handler : socketServer.getLoggingEventHandlers()) {
      if (project.getClient(handler.getRemoteAddress()) != null) {
        if (logger.isDebugEnabled()) {
          logger.debug("Adding LoggingEventWriter to " + handler);
        }
        handler.getLoggingEventListeners().add(writer);
      }
    }
  }


  /**
   * Removes the given logging event writer from all logging event handlers for
   * clients that belong to the given project.
   *
   * @param  writer  Writer to remove.
   * @param  project  Project for which clients' handlers should have writer
   * added.
   */
  private void removeLoggingEventWriter(
    final LoggingEventWriter writer,
    final ProjectConfig project)
  {
    for (LoggingEventHandler handler : socketServer.getLoggingEventHandlers()) {
      if (project.getClient(handler.getRemoteAddress()) != null) {
        if (logger.isDebugEnabled()) {
          logger.debug("Removing LoggingEventWriter from " + handler);
        }
        handler.getLoggingEventListeners().remove(writer);
      }
    }
  }


  /**
   * Form backing object for log watching configuration.
   *
   * @author  Middleware Services
   * @version  $Revision$
   */
  public static class WatchConfig
  {

    /** Default appender layout conversion pattern. */
    private static final String DEFAULT_CONVERSION_PATTERN =
      "%X{host} %d %-5p [%c] %m%n";

    /** ID of project whose logging events will be watched. */
    private int projectId;

    /** Appender layout string. */
    private String layoutConversionPattern = DEFAULT_CONVERSION_PATTERN;

    /** Array of enabled category IDs. */
    private int[] categoryIds;


    public WatchConfig(int projectId)
    {
      setProjectId(projectId);
    }

    /** @return  the projectId */
    public int getProjectId()
    {
      return projectId;
    }

    /** @param  projectId  the projectId to set */
    public void setProjectId(int projectId)
    {
      this.projectId = projectId;
    }

    /** @return  the layoutConversionPattern */
    @NotNull(message = "{watchConfig.layoutConversionPattern.notNull}")
    public String getLayoutConversionPattern()
    {
      return layoutConversionPattern;
    }

    /** @param  layoutConversionPattern  the layoutConversionPattern to set */
    public void setLayoutConversionPattern(String layoutConversionPattern)
    {
      this.layoutConversionPattern = layoutConversionPattern;
    }

    /** @return  the categoryIds */
    public int[] getCategoryIds()
    {
      return categoryIds;
    }

    /** @param  categoryIds  the categoryIds to set */
    public void setCategoryIds(int[] categoryIds)
    {
      this.categoryIds = categoryIds;
    }
  }


  /**
   * Handles writing logging events to the HTTP response stream.
   *
   * @author  Middleware Services
   * @version  $Revision$
   */
  private class LoggingEventWriter implements LoggingEventListener
  {
    private int waitCount;
    private PrintWriter writer;
    private LoggerRepository repository;
    private BlockingQueue<LoggingEvent> eventQueue =
      new ArrayBlockingQueue<LoggingEvent>(10);


    /**
     * Creates a new instance that writes events to the given logger hierarchy
     * using a {@link WriterAppender} wrapped around the given HTTP response.
     *
     * @param  repo  Logging repository.
     * @param  layout  Appender layout.
     * @param  response  HTTP response to which events are written.
     */
    public LoggingEventWriter(
      final LoggerRepository repo,
      final Layout layout,
      final HttpServletResponse response)
    {
      repository = repo;
      try {
        response.setContentType("text/plain");
        writer = response.getWriter();
        writer.println("Logging output will appear below as it is received.");
        writer.println("===================================================");
        writer.flush();
        response.flushBuffer();

        final WriterAppender appender = new WriterAppender();
        appender.setWriter(writer);
        appender.setLayout(layout);

        final Enumeration<?> e = repo.getCurrentLoggers();
        while (e.hasMoreElements()) {
          ((Logger) e.nextElement()).addAppender(appender);
        }
      } catch (IOException e) {
        throw new RuntimeException(
          "Failed initializing HTTP response stream writer.",
          e);
      }
    }


    /** {@inheritDoc}. */
    public void eventReceived(
      final LoggingEventHandler sender,
      final LoggingEvent event)
    {
      sender.setupMDC();
      if (waitCount > 0) {
        waitCount = 0;
        writer.println();
      }

      final Logger logger = repository.getLogger(event.getLoggerName());
      if (event.getLevel().isGreaterOrEqual(logger.getEffectiveLevel())) {
        logger.callAppenders(event);
      }
      writer.flush();
      eventQueue.offer(event);
    }


    /**
     * Blocks until the underlying {@link PrintWriter} encounters errors,
     * presumably on stream close, during which time logging events are written.
     */
    public void waitToFinish()
    {
      while (!writer.checkError()) {
        // Time out after a short wait to allow periodic stream close check
        // and provide user feedback that something is happening
        LoggingEvent event = null;
        try {
          event = eventQueue.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          if (logger.isDebugEnabled()) {
            logger.debug("Interrupted waiting for logging events.");
          }
          return;
        }
        if (event == null) {
          waitCount++;
          writer.print('.');
          writer.flush();
        }
      }
    }
  }
}
