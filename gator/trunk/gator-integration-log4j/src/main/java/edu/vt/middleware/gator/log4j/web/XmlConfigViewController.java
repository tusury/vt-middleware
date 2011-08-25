/*
  $Id: XmlConfigViewController.java 1421M 2010-06-25 16:43:59Z (local) $

  Copyright (C) 2009-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1421M $
  Updated: $Date: 2010-06-25 12:43:59 -0400 (Fri, 25 Jun 2010) $
*/
package edu.vt.middleware.gator.log4j.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.vt.middleware.gator.AppenderConfig;
import edu.vt.middleware.gator.AppenderParamConfig;
import edu.vt.middleware.gator.CategoryConfig;
import edu.vt.middleware.gator.LayoutParamConfig;
import edu.vt.middleware.gator.ParamConfig;
import edu.vt.middleware.gator.ProjectConfig;
import edu.vt.middleware.gator.server.SocketServer;
import edu.vt.middleware.gator.util.FileHelper;
import edu.vt.middleware.gator.util.StaxIndentationHandler;
import edu.vt.middleware.gator.web.AbstractController;
import edu.vt.middleware.gator.web.ControllerHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles delivering an XML view of the project configuration that could be
 * parsed by the log4j {@link DOMConfigurator}.
 *
 * @author  Middleware Services
 */
@Controller
public class XmlConfigViewController extends AbstractController
{
  /** log4j namespace. */
  public static final String NS_LOG4J = "http://jakarta.apache.org/log4j/";

  /** Name of socket appender. */
  public static final String SOCKET_APPENDER_NAME = "SOCKET";

  /** Server instance. */
  @Autowired
  private SocketServer socketServer;


  @RequestMapping(
    value = "/project/{projectName}/log4j.xml",
    method = RequestMethod.GET
  )
  public void getLog4jXml(
    @PathVariable("projectName") final String projectName,
    final Model model,
    final HttpServletRequest request,
    final HttpServletResponse response) throws Exception
  {
    ProjectConfig project = getProject(projectName);
    // Work on a clone so the original is unchanged
    project = ControllerHelper.cloneProject(project);

    final XMLStreamWriter writer =
      StaxIndentationHandler.createIndentingStreamWriter(
          response.getOutputStream());
    writer.writeStartDocument("UTF-8", "1.0");
    writer.writeStartElement("log4j", "configuration", NS_LOG4J);
    writer.writeStartElement("appender");
    writer.writeAttribute("name", SOCKET_APPENDER_NAME);
    writer.writeAttribute("class", "org.apache.log4j.net.SocketAppender");
    writeParam(writer, "RemoteHost", socketServer.getBindAddress());
    writeParam(writer, "Port", socketServer.getPort());
    writeParam(writer, "ReconnectionDelay", "60000");
    writeParam(writer, "Threshold", "ALL");
    writer.writeEndElement(); //appender
   
    for (AppenderConfig appender : project.getAppenders()) {
      // Update file appender paths to be suitable for clients
      final ParamConfig fileParam = appender.getAppenderParam("file");
      if (fileParam != null) {
        final String clientAppenderPath = FileHelper.pathCat(
          project.getClientLogDir(),
          fileParam.getValue());
        fileParam.setValue(clientAppenderPath);
      }
      writeAppender(writer, appender);
    }
    
    for (CategoryConfig category : project.getCategories()) {
      writeCategory(writer, category);
    }
    
    writer.writeEndElement(); //configuration
    writer.writeEndDocument();
    writer.close();
  }
  

  private void writeParam(
    final XMLStreamWriter w, final String name, final Object value)
    throws XMLStreamException
  {
    w.writeEmptyElement("param");
    w.writeAttribute("name", name);
    w.writeAttribute("value", value.toString());
  }


  private void writeAppender(
    final XMLStreamWriter w, final AppenderConfig appender)
    throws XMLStreamException
  {
    w.writeStartElement("appender");
    w.writeAttribute("name", appender.getName());
    w.writeAttribute("class", appender.getAppenderClassName());
    for (AppenderParamConfig p : appender.getAppenderParams()) {
      writeParam(w, p.getName(), p.getValue());
    }
    if (appender.getErrorHandlerClassName() != null) {
      w.writeEmptyElement("errorHandler");
      w.writeAttribute("class", appender.getErrorHandlerClassName());
    }
    if (appender.getLayoutClassName() != null) {
      w.writeStartElement("layout");
      w.writeAttribute("class", appender.getLayoutClassName());
      for (LayoutParamConfig p : appender.getLayoutParams()) {
        writeParam(w, p.getName(), p.getValue());
      }
      w.writeEndElement();
    }
    w.writeEndElement();
  }


  private void writeCategory(
    final XMLStreamWriter w, final CategoryConfig category)
    throws XMLStreamException
  {
    if (category.isRoot()) {
      w.writeStartElement("root");
    } else {
      w.writeStartElement("category");
      w.writeAttribute("name", category.getName());
      w.writeAttribute("additivity",
          Boolean.toString(category.getAdditivity()));
    }
    w.writeEmptyElement("priority");
    w.writeAttribute("value", category.getLevel());
    for (AppenderConfig p : category.getAppenders()) {
      w.writeEmptyElement("appender-ref");
      w.writeAttribute("ref", p.getName());
    }
    if (category.isAllowSocketAppender()) {
      w.writeEmptyElement("appender-ref");
      w.writeAttribute("ref", SOCKET_APPENDER_NAME);
    }
    w.writeEndElement();
  }
}
