<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>
<div class="clear"></div>

<div><strong>Version:</strong> <spring:message code="version"/></div>
<div><strong>Start Time:</strong> ${startTime}</div>
<fmt:formatNumber var="freeWidth" type="number" maxFractionDigits="0"
value="${600*freeMemory/(freeMemory+usedMemory)}" />
<fmt:formatNumber var="usedWidth" type="number" maxFractionDigits="0"
value="${600*usedMemory/(freeMemory+usedMemory)}" />
<div style="margin-bottom:.1em"><strong>Memory Usage:</strong>
<table style="font-size:.9em;padding:0;margin:0;border:2px inset #ccc" cellpadding="0" cellspacing="0">
  <tr>
    <td style="color:white;background-color:green;width:${freeWidth}px">Free: ${freeMemory} MB</td>
    <td style="color:black;background-color:orange;width:${usedWidth}px">Used: ${usedMemory} MB</td>
  </tr>
</table>
</div>
<div><a href="<c:url value="/secure/server/status.html"/>">More server status</a></div>

<h1>Projects</h1>

<div class="button_group">
  <a class="button" href="<c:url
    value="/secure/project/add.html" />">Create Project</a>
  <c:if test="${not empty projects}">
    <a class="button" href="<c:url
      value="/secure/project/copy.html" />">Copy Project</a>
  </c:if>
</div>

<table summary="Project listing">
<c:forEach items="${projects}" var="project" varStatus="stat">
  <tr class="button_row">
    <td>${stat.count}.</td>
    <td>${project.name}</td>
    <td class="button_cell">
      <security:accesscontrollist hasPermission="2" domainObject="${project}">
        <a class="button" href="<c:url value="/secure/project/${project.name}/edit.html" />">Edit</a>
      </security:accesscontrollist>
    </td>
    <td class="button_cell">
      <a class="button" href="<c:url value="/secure/project/${project.name}/watch.html" />">Watch&nbsp;Logs</a>
    </td>
    <td class="button_cell">
      <a class="button" href="<c:url value="/project/${project.name}/log4j.xml" />">Preview&nbsp;XML</a>
    </td>
    <td class="button_cell">
      <security:accesscontrollist hasPermission="8" domainObject="${project}">
        <a class="button" href="<c:url value="/secure/project/${project.name}/delete.html" />">Delete</a>
      </security:accesscontrollist>
    </td>
  </tr>
</c:forEach>
</table>

<%@ include file="includes/bottom.jsp" %>
