<%@ include file="includes/top.jsp" %>

<h1>Project Listing</h1>

<p>
<span class="button"><a href="<c:url
  value="/auth/project/add.html" />">Create Project</a></span>
<c:if test="${not empty model.projects}">
	<span class="button"><a href="<c:url
	  value="/auth/project/copy.html" />">Copy Project</a></span>
</c:if>

</p>

<table width="100%">
<c:forEach items="${model.projects}" var="project" varStatus="stat">
  <tr>
  <td class="button_row" style="font-size:1.1em">${stat.count}.</td>
  <td class="button_row" width="50%"
    style="font-size:1.1em">${project.name}</td>
  <td class="button_row">
		<span class="button">
		<a href="<c:url value="/auth/project/${project.name}/edit.html" />">Edit</a>
		</span>
  </td>
  <td class="button_row">
		<span class="button">
		<a href="<c:url value="/project/${project.name}/log4j.xml" />">Preview XML</a>
	  </span>
  </td>
  <td class="button_row">
		<span class="button">
		<a href="<c:url value="/auth/project/${project.name}/delete.html" />">Delete</a>
	  </span>
	  <span>Requires Confirmation</span>
  </td>
	</tr>
</c:forEach>
</table>

<%@ include file="includes/bottom.jsp" %>
