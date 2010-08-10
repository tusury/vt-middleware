<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<%@ include file="includes/top-taglib.jsp" %>
<%@ page contentType="text/xml" %>

<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

  <appender name="SOCKET" class="org.apache.log4j.net.SocketAppender">
    <param name="RemoteHost" value="${bindAddress}"/>
    <param name="Port" value="${port}"/>
    <param name="ReconnectionDelay" value="60000"/>
    <param name="Threshold" value="ALL"/>
  </appender>

<c:forEach items="${project.appenders}" var="appender">
	<appender name="${appender.name}" class="${appender.appenderClassName}">
	<c:if test="${not empty appender.errorHandlerClassName}">
	  <errorHandler class="${appender.errorHandlerClassName}"/>
	</c:if>
	<c:forEach items="${appender.appenderParams}" var="p">
	  <param name="${p.name}" value="${p.value}"/>
	</c:forEach>
	<c:if test="${not empty appender.layoutClassName}">
	  <layout class="${appender.layoutClassName}">
	  <c:forEach items="${appender.layoutParams}" var="p">
		  <param name="${p.name}" value="${p.value}"/>
		</c:forEach>
	  </layout>
	</c:if>
	</appender>
</c:forEach>

<c:forEach items="${project.categories}" var="category">
  <c:choose>
  <c:when test="${category.root}">
	<root>
	</c:when>
	<c:otherwise>
	<category name="${category.name}" additivity="${category.additivity}">
	</c:otherwise>
	</c:choose>
		<priority value="${category.level}" />
		<c:forEach items="${category.appenders}" var="appender">
	  <appender-ref ref="${appender.name}" />
		</c:forEach>
		<c:if test="${category.allowSocketAppender}">
		<appender-ref ref="SOCKET" />
		</c:if>
  <c:choose>
  <c:when test="${category.root}">
  </root>
  </c:when>
  <c:otherwise>
	</category>
	</c:otherwise>
	</c:choose>
</c:forEach>
</log4j:configuration>