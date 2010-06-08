<%@ include file="includes/top.jsp" %>
<div class="clear"></div>

<div id="title">Project Listing</div>

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
