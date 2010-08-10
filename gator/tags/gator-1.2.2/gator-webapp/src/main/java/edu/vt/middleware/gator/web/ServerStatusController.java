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

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.log4j.LoggingEventHandler;
import edu.vt.middleware.gator.log4j.SocketServer;

/**
 * View for rendering server status information.
 *
 * @author Middleware
 * @version $Revision$
 *
 */
@Controller
@RequestMapping("/secure")
public class ServerStatusController extends AbstractController
{
  /** Socket server. */
  @Autowired @NotNull
  private SocketServer socketServer;


  @RequestMapping(
    value = "/server/status.html",
    method = RequestMethod.GET
  )
  public String getServerStatus(final Model model)
  {
    final Date startTime = socketServer.getStartTime();
    model.addAttribute("startTime", startTime);
    model.addAttribute("upTime", ControllerHelper.calculateUpTime(startTime));
    model.addAttribute("serverAddress", socketServer.getBindAddress());
    model.addAttribute("serverPort", socketServer.getPort());
    model.addAttribute("maxClients", socketServer.getMaxClients());
    model.addAttribute(
        "clientRemovalPolicy",
        socketServer.getClientRemovalPolicy().getClass().getName());
    final long freeMem = Runtime.getRuntime().freeMemory();
    final long usedMem = Runtime.getRuntime().totalMemory() - freeMem;
    model.addAttribute("freeMemory", freeMem / 1024 / 1024);
    model.addAttribute("usedMemory", usedMem / 1024 / 1024);
    
    final List<ClientInfo> clients = new ArrayList<ClientInfo>();
    for (LoggingEventHandler handler : socketServer.getLoggingEventHandlers()) {
      clients.add(createClientInfo(handler));
    }
    model.addAttribute("clients", clients);

    return "serverStatus";
  }


  @RequestMapping(
    value = "/client/{clientAddress}/status.html",
    method = RequestMethod.GET
  )
  public String getClientStatus(
    @PathVariable("clientAddress") final String clientAddress,
    final HttpServletResponse response,
    final Model model)
  {
    final LoggingEventHandler handler =
      socketServer.getLoggingEventHandler(clientAddress);
    if (handler == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }
    model.addAttribute("client", createClientInfo(handler));
    model.addAttribute("threshold", handler.getRepository().getThreshold());
    final SortedSet<LoggerInfo> loggers = new TreeSet<LoggerInfo>();
    loggers.add(createLoggerInfo(handler.getRepository().getRootLogger()));
    final Enumeration<?> loggerEnum =
      handler.getRepository().getCurrentLoggers();
    while (loggerEnum.hasMoreElements()) {
      loggers.add(createLoggerInfo((Logger) loggerEnum.nextElement()));
    }
    model.addAttribute("loggers", loggers);
    return "clientStatus";
  }


  @RequestMapping(
    value = "/client/{clientAddress}/disconnect.html",
    method = RequestMethod.GET
  )
  public String disconnect(
    @PathVariable("clientAddress") final String clientAddress,
    final HttpServletResponse response,
    final Model model)
  {
    final LoggingEventHandler handler =
      socketServer.getLoggingEventHandler(clientAddress);
    if (handler == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }
    try {
      handler.getSocket().close();
    } catch (Exception e) {
      logger.error("Error closing socket for client " + clientAddress, e);
    }
    return "redirect:/secure/server/status.html";
  }


  private ClientInfo createClientInfo(final LoggingEventHandler handler)
  {
    final String host = handler.getRemoteAddress().getHostName();
    final String addr = handler.getRemoteAddress().getHostAddress();
    final ClientInfo client = new ClientInfo();
    client.setName(host);
    client.setConnectedAt(handler.getStartTime());
    client.setLoggingEventCount(handler.getLoggingEventCount());
    for (ProjectConfig p : configManager.findProjectsByClientName(host)) {
      client.setProject(p);
    }
    if (client.getProject() == null) {
      // Try to find project by client IP address
      for (ProjectConfig p : configManager.findProjectsByClientName(addr)) {
        client.setProject(p);
      }
    }
    return client;
  }


  /**
   * Creates a {@link LoggerInfo} instance from a log4j {@link Logger} instance.
   * 
   * @param  logger  Log4j logger.
   *
   * @return  Logger information for Web display.
   */
  @SuppressWarnings("unchecked")
  private LoggerInfo createLoggerInfo(final Logger logger)
  {
    final LoggerInfo info = new LoggerInfo();
    info.setCategory(logger.getName());
    if (logger.getLevel() != null) {
      info.setLevel(logger.getLevel().toString());
    }
    info.setEffectiveLevel(logger.getEffectiveLevel().toString());
    info.setAdditivity(logger.getAdditivity());
    Category current = logger;
    boolean isAdditive = false;
    do {
      final Enumeration e = current.getAllAppenders();
      while (e.hasMoreElements()) {
        info.getAppenders().add(((Appender) e.nextElement()).getName());
      }
      isAdditive = current.getAdditivity();
	    current = current.getParent();
    } while (current != null && isAdditive);
    
    return info;
  }
}
