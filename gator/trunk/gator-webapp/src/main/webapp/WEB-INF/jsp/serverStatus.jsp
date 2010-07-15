<%@ include file="includes/top.jsp" %>
<%@ include file="includes/user.jsp" %>

<div id="crumbs">
  <ul>
    <li class="first"><a class="navlink" href="<c:url value="/secure/project/list.html" />">Project Listing</a></li>
    <li>Server Status</li>
  </ul>
</div>
<div class="clear"></div>

<div id="title">Server Status</div>

<div><strong>Start Time:</strong> ${startTime}</div>
<div><strong>UpTime:</strong> ${upTime}</div>
<div><strong>Server Address:</strong> ${serverAddress}</div>
<div><strong>Server Port:</strong> ${serverPort}</div>
<div><strong>Max Allowed Clients:</strong> ${maxClients}</div>
<div><strong>Client Removal Policy:</strong> ${clientRemovalPolicy}</div>
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

<h1>Connected Clients</h1>
<table summary="Connected client information">
  <tr>
    <th>&nbsp;</th>
    <th>Client</th>
    <th>Project</th>
    <th>Connected At</th>
    <th>Event Count</th>
    <th>&nbsp;</th>
  </tr>
	<c:forEach items="${clients}" var="client" varStatus="stat">
	  <tr class="button_row">
	    <td>${stat.count}.</td>
	    <td><a href="<c:url
	      value="/secure/client/${client.name}/status.html"/>">${client.name}</a></td>
	    <td>
        <security:accesscontrollist hasPermission="2" domainObject="${client.project}">
          <a href="<c:url value="/secure/project/${client.project.name}/edit.html"/>">
	      </security:accesscontrollist>
	      ${client.project.name}
        <security:accesscontrollist hasPermission="2" domainObject="${client.project}">
          </a>
	      </security:accesscontrollist>
	    </td>
	    <td>${client.connectedAt}</td>
	    <td>${client.loggingEventCount}</td>
	    <td class="button_cell">
        <security:accesscontrollist hasPermission="2" domainObject="${client.project}">
			    <a class="button" href="<c:url
			      value="/secure/client/${client.name}/disconnect.html"/>">Disconnect</a></td>
	      </security:accesscontrollist>
	    </td>
	  </tr>
	</c:forEach>
</table>

<%@ include file="includes/bottom.jsp" %>
