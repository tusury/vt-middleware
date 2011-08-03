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
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.server.LoggingEventHandler;
import edu.vt.middleware.gator.server.SocketServer;
import edu.vt.middleware.gator.server.status.ClientStatus;

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
    
    final List<ClientStatus> clients = new ArrayList<ClientStatus>();
    for (LoggingEventHandler handler : socketServer.getLoggingEventHandlers()) {
      clients.add(createClientStatus(handler));
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
    model.addAttribute("client", createClientStatus(handler));
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


  private ClientStatus createClientStatus(final LoggingEventHandler handler)
  {
    final String host = handler.getRemoteAddress().getHostName();
    final String addr = handler.getRemoteAddress().getHostAddress();
    final ClientStatus clientStatus = new ClientStatus(handler);
    for (ProjectConfig p : configManager.findProjectsByClientName(host)) {
      clientStatus.setProject(p);
    }
    if (clientStatus.getProject() == null) {
      // Try to find project by client IP address
      for (ProjectConfig p : configManager.findProjectsByClientName(addr)) {
        clientStatus.setProject(p);
      }
    }
    handler.getLoggingEngine().configure(clientStatus);
    return clientStatus;
  }
}
