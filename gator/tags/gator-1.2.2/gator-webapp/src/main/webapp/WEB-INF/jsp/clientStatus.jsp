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
<div>
  <strong>Project:</strong>
  <security:accesscontrollist hasPermission="2" domainObject="${client.project}">
    <a href="<c:url value="/secure/project/${client.project.name}/edit.html"/>">
  </security:accesscontrollist>
  ${client.project.name}
  <security:accesscontrollist hasPermission="2" domainObject="${client.project}">
    </a>
  </security:accesscontrollist>
</div>
<div><strong>Connected At:</strong> ${client.connectedAt}</div>
<div><strong>Event Count:</strong> ${client.loggingEventCount}</div>

<h1>Logger Repository Details</h1>
<div><strong>Threshold:</strong> ${threshold}</div>
<h2>Loggers</h2>
<table summary="Logger repository logger detail">
  <tr>
    <th><small>Category</small></th>
    <th><small>Level</small></th>
    <th><small>Effective</small></th>
    <th><small>Additivity</small></th>
    <th><small>Appenders</small></th>
  </tr>
  <c:forEach items="${loggers}" var="logger" varStatus="stat">
    <tr>
      <td><small>${logger.category}</small></td>
      <td><small>${logger.level}</small></td>
      <td><small>${logger.effectiveLevel}</small></td>
      <td><small>${logger.additivity}</small></td>
      <td><small>
        <c:forEach items="${logger.appenders}"
          var="appender"
          varStatus="st"><c:if test="${st.count > 1}">,</c:if>
          ${appender}</c:forEach></small>
      </td>
    </tr>
  </c:forEach>
</table>

<%@ include file="includes/bottom.jsp" %>
