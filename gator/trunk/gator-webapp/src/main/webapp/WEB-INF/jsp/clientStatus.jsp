<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li><a class="navlink" href="<c:url value="/secure/server/status.html" />">Server Status</a></li>
    <li>${client.name} Status</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Client Status</div>

<div style="margin-bottom:5px">
  <strong>Name:</strong> ${client.name}
  <security:accesscontrollist hasPermission="2" domainObject="${client.project}">
    <a class="button" href="<c:url
      value="/secure/client/${client.name}/disconnect.html"/>">Disconnect</a></td>
  </security:accesscontrollist>
</div>
<div><strong>Connected At:</strong> ${client.connectedAt}</div>
<div><strong>Event Count:</strong> ${client.loggingEventCount}</div>

<h1>Logger Repository Details</h1>
<div><strong>Root Category:</strong> ${client.repository.rootLogger.name}</div>
<div><strong>Threshold:</strong> ${client.repository.threshold}</div>
<h2>Loggers</h2>
<table summary="Logger repository logger detail">
  <tr>
    <th>&nbsp;</th>
    <th>Category</th>
    <th>Level</th>
    <th>Effective</th>
    <th>Additivity</th>
    <th>Appenders</th>
  </tr>
  <c:forEach items="${client.repository.currentLoggers}" var="logger" varStatus="stat">
    <tr class="button_row">
      <td>${stat.count}.</td>
      <td>${logger.name}</td>
      <td>${logger.level}</td>
      <td>${logger.effectiveLevel}</td>
      <td>${logger.additivity}</td>
      <td>
        <c:forEach items="${logger.allAppenders}"
          var="appender"
          varStatus="stat"><c:if test="${stat.count > 1}">,</c:if>
          ${appender.name}</c:forEach>
      </td>
    </tr>
  </c:forEach>
</table>

<%@ include file="includes/bottom.jsp" %>
