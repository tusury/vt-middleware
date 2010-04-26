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
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.log4j.LoggingEventCollector;
import edu.vt.middleware.gator.log4j.LoggingEventHandler;
import edu.vt.middleware.gator.log4j.SocketServer;

import org.apache.log4j.Hierarchy;
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
 * Controller for watching collected logging events in real time.
 * Provides an form interface for configuring the categories to display,
 * then displays the logging events from those categories in the HTTP response
 * as raw plain/text data.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
@SessionAttributes({"watchConfig", "project"})
public class LogWatcherFormController extends AbstractFormController
{
  public static final String VIEW_NAME = "watchForm";

  /** Socket server */
  @Autowired
  @NotNull
  private SocketServer socketServer;


  @RequestMapping(
      value = "/project/{projectName}/watch.html",
      method = RequestMethod.GET) 
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
      method = RequestMethod.POST)
  @Transactional(propagation = Propagation.REQUIRED)
  public String watch(
      @Valid @ModelAttribute("watchConfig") final WatchConfig watchConfig,
      final BindingResult result,
      final HttpServletResponse response)
  {
    if (result.hasErrors()) {
      return VIEW_NAME;
    }
    final LoggingEventCollector collector = new LoggingEventCollector(1000);
    try {
      addLoggingEventCollector(collector);
      writeResponse(response, watchConfig, collector);
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


    public WatchConfig(int projectId)
    {
      setProjectId(projectId);
    }

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
    @NotNull(message = "{watchConfig.layoutConversionPattern.notNull}")
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
